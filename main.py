import httpx
import asyncio
import re
import json
import os
import google.generativeai as genai
from fastapi import FastAPI, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from pydantic import BaseModel
from typing import List, Optional
from datetime import datetime, timedelta
from jose import JWTError, jwt
from contextlib import asynccontextmanager
from bs4 import BeautifulSoup

# ==========================================
# 1. CẤU HÌNH HỆ THỐNG
# ==========================================
SECRET_KEY = "scam_warning_super_secret_6688"
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 1440 
DB_FILE = "scam_db.json"
UPDATE_INTERVAL_HOURS = 12 

# --- CẤU HÌNH GEMINI AI ---
GEMINI_API_KEY = "AIzaSyDlX-IIMHDeX6ct4mCmCKovPQ1qmiW8jVU"
genai.configure(api_key=GEMINI_API_KEY)

def get_best_available_model():
    """Tự động chọn mô hình tốt nhất từ danh sách Google cung cấp cho bạn"""
    try:
        available = [m.name for m in genai.list_models() 
                    if 'generateContent' in m.supported_generation_methods]
        print(f"--- [AI INFO] Các mô hình khả dụng: {available} ---")
        
        priority = ['gemini-2.0-flash', 'gemini-1.5-flash', 'gemini-flash-latest', 'gemini-pro']
        for p_name in priority:
            for a_name in available:
                if p_name in a_name:
                    print(f"--- [AI INFO] Đã chọn mô hình: {a_name} ---")
                    return genai.GenerativeModel(a_name)
        if available:
            return genai.GenerativeModel(available[0])
    except Exception as e:
        print(f"--- [AI ERROR] Lỗi liệt kê model: {e}. Dùng mặc định flash-latest ---")
    return genai.GenerativeModel('gemini-1.5-flash')

ai_model = get_best_available_model()
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="api/auth/login")

# ==========================================
# 2. DANH SÁCH 18 NGUỒN DỮ LIỆU
# ==========================================
SOURCES = [
    {"name": "ChongLuaDao", "type": "raw", "url": "https://raw.githubusercontent.com/chongluadao/vntxt/main/phone.txt", "enabled": True},
    {"name": "TrangTrang", "type": "html", "url": "https://www.trangtrang.com/", "enabled": True},
    {"name": "SpamCallCheck VN", "type": "html", "url": "https://spamcallcheck.com/vi", "enabled": True},
    {"name": "CheckScam VN", "type": "html", "url": "https://www.checkscam.vn/", "enabled": True},
    {"name": "VTrust Scam Check", "type": "html", "url": "https://vtrust.vn/en/check-scam", "enabled": True},
    {"name": "Admin367", "type": "html", "url": "https://admin367.vn/", "enabled": True},
    {"name": "BlacklistDB", "type": "html", "url": "https://blacklistdb.vercel.app/", "enabled": True},
    {"name": "Tellows", "type": "html", "url": "https://www.tellows.com/", "enabled": True},
    {"name": "800Notes", "type": "html", "url": "https://800notes.com/", "enabled": True},
    {"name": "WhoCallsMe", "type": "html", "url": "https://whocallsme.com/", "enabled": True},
    {"name": "CallerHouse", "type": "html", "url": "https://callerhouse.com/", "enabled": True},
    {"name": "SpamCalls", "type": "html", "url": "https://spamcalls.net/en/", "enabled": True},
    {"name": "ScamNumbers", "type": "html", "url": "https://www.scamnumbers.info/", "enabled": True},
    {"name": "PhoneSpamAlerts", "type": "html", "url": "https://phonespamalerts.com/", "enabled": True},
    {"name": "NumLookup", "type": "html", "url": "https://www.numlookup.com/", "enabled": True},
    {"name": "SyncMe", "type": "html", "url": "https://sync.me/", "enabled": True},
    {"name": "CallFilter", "type": "html", "url": "https://callfilter.app/", "enabled": True},
]

# ==========================================
# 3. MODELS (KHỚP VỚI ANDROID APP)
# ==========================================
class SmsRequest(BaseModel):
    phone_number: str
    message: str
    device_id: str

class CallRequest(BaseModel):
    phone_number: str
    transcript: str
    call_time: str

class ReportRequest(BaseModel):
    phone_number: str
    report_type: str
    description: str

class AiLogRequest(BaseModel):
    source_type: str
    processing_mode: str
    risk_score: int
    processing_time: float

class AnalysisResponse(BaseModel):
    risk_score: int
    is_scam: bool
    category: Optional[str] = None
    reasons: Optional[List[str]] = None
    warning_message: Optional[str] = None

class RegisterRequest(BaseModel):
    phone_number: str
    device_id: str
    device_model: Optional[str] = "Unknown"
    os_version: Optional[str] = "Unknown"

class AuthResponse(BaseModel):
    success: bool
    access_token: str
    refresh_token: str
    expires_in: int

# ==========================================
# 4. DATABASE & HELPERS
# ==========================================
SCAM_DATABASE = []
KEYWORDS_BLACKLIST = ["nợ", "khóa", "bank", "otp", "công an", "viện kiểm sát", "chuyển khoản", "trúng thưởng", "shopee", "tiki"]
PHONE_REGEX = r"(?:\+?\d{1,3}[\s.-]?)?\(?\d{3}\)?[\s.-]?\d{3}[\s.-]?\d{4}"

def create_access_token(data: dict):
    to_encode = data.copy()
    expire = datetime.utcnow() + timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    to_encode.update({"exp": expire})
    return jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)

async def get_current_user(token: str = Depends(oauth2_scheme)):
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        return payload.get("sub")
    except:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Token không hợp lệ")

async def analyze_with_gemini(text: str, source_type: str):
    prompt = f"""
    Bạn là chuyên gia an ninh mạng Việt Nam. Hãy phân tích {source_type} sau đây có phải lừa đảo không.
    Nội dung: "{text}"
    Yêu cầu trả về DUY NHẤT một chuỗi JSON chuẩn:
    {{
        "is_scam": boolean,
        "risk_score": integer (0-100),
        "category": "Loại lừa đảo",
        "reason": "Lý do ngắn gọn"
    }}
    """
    try:
        response = await asyncio.to_thread(ai_model.generate_content, prompt)
        json_match = re.search(r'\{.*\}', response.text, re.DOTALL)
        if json_match:
            return json.loads(json_match.group())
    except Exception as e:
        print(f"--- [AI ERROR] {e} ---")
    return None

# ==========================================
# 5. DB & CRAWL (LỌC TRÙNG & TÍCH LŨY)
# ==========================================
def save_db():
    with open(DB_FILE, "w", encoding="utf-8") as f:
        json.dump(SCAM_DATABASE, f, ensure_ascii=False, indent=4)

def load_db():
    global SCAM_DATABASE
    if os.path.exists(DB_FILE):
        try:
            with open(DB_FILE, "r", encoding="utf-8") as f:
                SCAM_DATABASE = json.load(f)
            print(f"--- [DB] Đã nạp {len(SCAM_DATABASE)} số lừa đảo ---")
        except: SCAM_DATABASE = []

async def fetch_blacklist_data():
    global SCAM_DATABASE
    load_db()
    initial_count = len(SCAM_DATABASE)
    existing_numbers = {item["phone_number"] for item in SCAM_DATABASE}
    headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/122.0.0.0'}

    async with httpx.AsyncClient(headers=headers, timeout=20.0, follow_redirects=True) as client:
        for source in SOURCES:
            if not source["enabled"]: continue
            try:
                resp = await client.get(source["url"])
                if resp.status_code == 200:
                    new_count = 0
                    if source["type"] == "raw":
                        for line in resp.text.splitlines():
                            phone = line.strip()
                            if len(phone) >= 10 and phone not in existing_numbers:
                                SCAM_DATABASE.append({"phone_number": phone, "category": "Blacklist", "report_count": 1})
                                existing_numbers.add(phone)
                                new_count += 1
                    elif source["type"] == "html":
                        soup = BeautifulSoup(resp.text, 'html.parser')
                        for s in soup(["script", "style"]): s.decompose()
                        found = re.findall(PHONE_REGEX, soup.get_text())
                        for p in found:
                            clean_p = re.sub(r"[\s.\-()]", "", p)
                            if 10 <= len(clean_p) <= 15 and clean_p not in existing_numbers:
                                SCAM_DATABASE.append({"phone_number": clean_p, "category": "Reported", "report_count": 1})
                                existing_numbers.add(clean_p)
                                new_count += 1
                    print(f"--- [CRAWL] {source['name']}: +{new_count} số mới ---")
            except: continue
    if len(SCAM_DATABASE) > initial_count: save_db()

async def schedule_updates():
    while True:
        await asyncio.sleep(UPDATE_INTERVAL_HOURS * 3600)
        await fetch_blacklist_data()

@asynccontextmanager
async def lifespan(app: FastAPI):
    await fetch_blacklist_data()
    update_task = asyncio.create_task(schedule_updates())
    yield
    update_task.cancel()

app = FastAPI(title="Scam Warning Backend", lifespan=lifespan)

# ==========================================
# 6. ENDPOINTS
# ==========================================

@app.post("/api/auth/register", response_model=AuthResponse)
async def register(request: RegisterRequest):
    token = create_access_token(data={"sub": request.phone_number})
    return {"success": True, "access_token": token, "refresh_token": "ref_" + token[-10:], "expires_in": 86400}

@app.post("/api/analyze/sms", response_model=AnalysisResponse)
async def analyze_sms(request: SmsRequest, current_user: str = Depends(get_current_user)):
    # 1. Check Blacklist
    blacklisted = next((item for item in SCAM_DATABASE if item["phone_number"] == request.phone_number), None)
    if blacklisted:
        return {"risk_score": 100, "is_scam": True, "category": "Danh sách đen", 
                "reasons": [f"Số điện thoại này đã bị cộng đồng báo cáo {blacklisted.get('report_count', 1)} lần."], 
                "warning_message": "HÃY CHẶN SỐ NÀY NGAY LẬP TỨC!"}

    # 2. AI Analysis
    ai_res = await analyze_with_gemini(request.message, "tin nhắn SMS")
    if ai_res:
        return {"risk_score": ai_res['risk_score'], "is_scam": ai_res['is_scam'], "category": ai_res['category'],
                "reasons": [ai_res['reason']], "warning_message": "Phân tích bởi AI Gemini"}

    # 3. Fallback
    matched = [w for w in KEYWORDS_BLACKLIST if w in request.message.lower()]
    return {"risk_score": 85 if matched else 5, "is_scam": bool(matched), "category": "Nghi ngờ",
            "reasons": [f"Chứa từ khóa nhạy cảm: {', '.join(matched)}"] if matched else [], 
            "warning_message": "Kiểm tra bằng từ khóa"}

@app.post("/api/analyze/call", response_model=AnalysisResponse)
async def analyze_call(request: CallRequest, current_user: str = Depends(get_current_user)):
    blacklisted = next((item for item in SCAM_DATABASE if item["phone_number"] == request.phone_number), None)
    if blacklisted:
        return { "risk_score": 100, "is_scam": True, "category": "Danh sách đen", 
                 "reasons": [f"Số lừa đảo xác nhận ({blacklisted.get('report_count', 1)} lượt báo cáo)."], 
                 "warning_message": "CÚP MÁY NGAY!" }

    ai_res = await analyze_with_gemini(request.transcript, "hội thoại cuộc gọi")
    if ai_res:
        return { "risk_score": ai_res['risk_score'], "is_scam": ai_res['is_scam'], "category": ai_res['category'], 
                 "reasons": [ai_res['reason']], "warning_message": "CÚP MÁY NGAY!" if ai_res['is_scam'] else "Bình thường." }
    
    return {"risk_score": 10, "is_scam": False, "category": "An toàn", "reasons": [], "warning_message": "Đang theo dõi."}

@app.post("/api/ai/log")
async def log_ai_result(request: AiLogRequest, current_user: str = Depends(get_current_user)):
    print(f"--- [AI LOG] {current_user}: {request.source_type} | Score: {request.risk_score}% ---")
    return {"status": "success"}

@app.get("/api/blacklist")
async def get_blacklist():
    # Trả về kèm theo số lần báo cáo cho App hiển thị
    return {"data": [
        {
            "phone_number": item["phone_number"],
            "category": item.get("category", "Lừa đảo"),
            "risk_level": 100,
            "report_count": item.get("report_count", 1)
        } for item in SCAM_DATABASE
    ]}

@app.post("/api/report")
async def submit_report(request: ReportRequest, current_user: str = Depends(get_current_user)):
    global SCAM_DATABASE
    phone = request.phone_number.strip()
    existing = next((item for item in SCAM_DATABASE if item["phone_number"] == phone), None)
    if existing:
        existing["report_count"] = existing.get("report_count", 1) + 1
    else:
        SCAM_DATABASE.append({"phone_number": phone, "category": request.report_type, "report_count": 1})
    save_db()
    return {"success": True, "message": "Đã ghi nhận báo cáo!"}

@app.get("/api/scam-keywords")
async def get_keywords():
    return {"keywords": KEYWORDS_BLACKLIST}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)