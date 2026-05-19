**TÀI LIỆU BỔ SUNG KỸ THUẬT**

Hệ thống Ứng dụng Cảnh báo Lừa đảo qua SMS và Cuộc gọi

SCAM WARNING APP — SUPPLEMENT DOCUMENT

*Phiên bản 1.0 — Bổ sung & sửa lỗi từ đánh giá Gap Analysis*
# **1. SỬA LỖI MÂU THUẪN TRONG TÀI LIỆU**

## **1.1. Làm rõ AI Engine: Gemma-2B vs TensorFlow Lite**
Thuyết Minh Đề Tài ghi công nghệ AI là Gemma-2B (Offline AI), nhưng Android Technical Architecture Document chỉ đề cập TensorFlow Lite (future) mà không nhắc Gemma-2B. Đây là mâu thuẫn cần thống nhất.

### **Quyết định thống nhất:**

|**Tình huống**|**Engine sử dụng**|**Ghi chú**|
| :- | :- | :- |
|Offline Local AI (trên thiết bị)|Gemma-2B via TensorFlow Lite (GGUF/TFLite format)|Phân tích nhanh khi không có internet|
|Online Cloud AI (FastAPI)|Python NLP + Rule-based Engine|Phân tích sâu, cập nhật blacklist|
|Future expansion|Custom ML model (scikit-learn / PyTorch)|Train từ community report data|

### **Cách tích hợp Gemma-2B trên Android:**
1. Download model file: gemma-2b-it-q4\_k\_m.gguf (khoảng 1.5GB)
1. Lưu vào internal storage: /data/data/com.scamwarning/files/models/
1. Load qua TensorFlow Lite hoặc llama.cpp Android binding
1. Chỉ dùng cho offline inference khi mất internet

## **1.2. Sửa mâu thuẫn ngôn ngữ lập trình Android**
Thuyết Minh ghi Android (Java/Kotlin) nhưng Android Technical Architecture chỉ nói Kotlin + Jetpack Compose.

|**Quyết định**|**Ngôn ngữ**|**Lý do**|
| :- | :- | :- |
|PRIMARY|Kotlin 100%|Jetpack Compose bắt buộc dùng Kotlin|
|DEPRECATED|Java — không dùng|Không tương thích tốt với Compose coroutine|

# **2. BỔ SUNG API XÁC THỰC (AUTHENTICATION)**
Tài liệu API Specification hiện tại có JWT nhưng THIẾU endpoint đăng ký, đăng nhập, và refresh token.

## **2.1. POST /api/auth/register — Đăng ký tài khoản**
### **Request Body:**
{

`  `"phone\_number": "0901234567",

`  `"device\_id": "android\_device\_001",

`  `"device\_model": "Samsung Galaxy A54",

`  `"os\_version": "Android 14"

}

### **Response Success (201):**
{

`  `"success": true,

`  `"user\_id": 1001,

`  `"access\_token": "eyJhbGciOiJIUzI1NiIs...",

`  `"refresh\_token": "dGhpcyBpcyBhIHJlZnJlc2g...",

`  `"expires\_in": 86400

}

## **2.2. POST /api/auth/login — Đăng nhập**
### **Request Body:**
{

`  `"phone\_number": "0901234567",

`  `"device\_id": "android\_device\_001"

}

### **Response Success (200): Tương tự register**

## **2.3. POST /api/auth/refresh — Làm mới token**
### **Request Body:**
{

`  `"refresh\_token": "dGhpcyBpcyBhIHJlZnJlc2g..."

}

### **Response Success (200):**
{

`  `"access\_token": "eyJhbGciOiJIUzI1NiIs...",

`  `"expires\_in": 86400

}

## **2.4. POST /api/auth/logout — Đăng xuất**
Header: Authorization: Bearer <access\_token>

Response: { "success": true }

## **2.5. API Trusted Number Management**
### **GET /api/trusted-numbers — Lấy danh sách số tin cậy**
### **POST /api/trusted-numbers — Thêm số tin cậy**
{

`  `"phone\_number": "0901234567",

`  `"label": "Ba"

}
### **DELETE /api/trusted-numbers/{phone\_number} — Xóa số tin cậy**

## **2.6. GET /api/user/settings — Cài đặt người dùng**
{

`  `"notification\_enabled": true,

`  `"auto\_analyze\_sms": true,

`  `"risk\_threshold\_notification": 30,

`  `"risk\_threshold\_overlay": 60,

`  `"risk\_threshold\_block\_suggestion": 85,

`  `"call\_analysis\_enabled": true,

`  `"offline\_mode": false

}

## **2.7. PUT /api/user/settings — Cập nhật cài đặt**
Request Body: Bất kỳ field nào trong GET /api/user/settings

# **3. CẤU TRÚC PROJECT FASTAPI BACKEND**

## **3.1. Cấu trúc thư mục**
scam-warning-backend/

|-- app/

|   |-- main.py                  # FastAPI app init

|   |-- config.py                # Settings, env variables

|   |-- database.py              # MySQL connection (SQLAlchemy)

|   |-- models/                  # SQLAlchemy ORM models

|   |   |-- user.py

|   |   |-- scam\_phone.py

|   |   |-- sms\_history.py

|   |   |-- call\_history.py

|   |   |-- report.py

|   |   |-- ai\_log.py

|   |-- schemas/                 # Pydantic request/response schemas

|   |   |-- auth.py

|   |   |-- sms.py

|   |   |-- call.py

|   |   |-- report.py

|   |-- routers/                 # API route handlers

|   |   |-- auth.py              # /api/auth/\*

|   |   |-- sms.py              # /api/analyze/sms

|   |   |-- call.py             # /api/analyze/call

|   |   |-- report.py           # /api/report

|   |   |-- blacklist.py        # /api/blacklist

|   |   |-- user.py             # /api/user/\*

|   |-- services/               # Business logic

|   |   |-- ai\_service.py       # AI detection logic

|   |   |-- risk\_score.py       # Risk Score Engine

|   |   |-- keyword\_service.py  # Keyword detection

|   |   |-- url\_service.py      # URL detection

|   |   |-- blacklist\_service.py

|   |-- middleware/

|   |   |-- auth\_middleware.py  # JWT verification

|   |   |-- rate\_limiter.py

|   |-- utils/

|   |   |-- jwt\_utils.py

|   |   |-- phone\_utils.py      # Phone number validation

|-- requirements.txt

|-- .env                         # Environment variables

|-- Dockerfile

|-- docker-compose.yml

## **3.2. Environment Variables (.env)**
DATABASE\_URL=mysql+pymysql://user:pass@localhost:3306/scam\_warning

SECRET\_KEY=your-jwt-secret-key-256-bit

ALGORITHM=HS256

ACCESS\_TOKEN\_EXPIRE\_MINUTES=1440

REFRESH\_TOKEN\_EXPIRE\_DAYS=30

RATE\_LIMIT\_PER\_MINUTE=100

CORS\_ORIGINS=["http://localhost:3000"]

## **3.3. main.py khởi tạo**
from fastapi import FastAPI

from fastapi.middleware.cors import CORSMiddleware

from app.routers import auth, sms, call, report, blacklist, user

app = FastAPI(title='Scam Warning API', version='1.0.0')

app.add\_middleware(CORSMiddleware, allow\_origins=['\*'],

`    `allow\_methods=['\*'], allow\_headers=['\*'])

app.include\_router(auth.router, prefix='/api/auth', tags=['Auth'])

app.include\_router(sms.router, prefix='/api', tags=['SMS'])

app.include\_router(call.router, prefix='/api', tags=['Call'])

app.include\_router(report.router, prefix='/api', tags=['Report'])

app.include\_router(blacklist.router, prefix='/api', tags=['Blacklist'])

app.include\_router(user.router, prefix='/api/user', tags=['User'])

# **4. BỔ SUNG THIẾT KẾ CƠ SỞ DỮ LIỆU**

## **4.1. Bảng ai\_logs (bị thiếu trong tài liệu gốc)**
Bảng này được nhắc đến trong Android Technical Architecture nhưng chưa có schema trong Thiet\_Ke\_Co\_So\_Du\_Lieu.

|**Tên trường**|**Kiểu dữ liệu**|**Ràng buộc**|**Mô tả**|
| :- | :- | :- | :- |
|id|BIGINT|PK, AUTO\_INCREMENT|Mã log|
|source\_type|ENUM('SMS','CALL')|NOT NULL|Loại nguồn|
|phone\_number|VARCHAR(20)|NOT NULL|Số điện thoại|
|processing\_mode|ENUM('ONLINE','OFFLINE')|NOT NULL|Chế độ xử lý|
|risk\_score|INT|DEFAULT 0|Điểm rủi ro|
|is\_scam|BOOLEAN|DEFAULT FALSE|Kết quả|
|category|VARCHAR(50)|NULL|Loại scam|
|keyword\_matched|TEXT|NULL|Từ khóa phát hiện (JSON array)|
|processing\_time\_ms|INT|NULL|Thời gian xử lý (ms)|
|model\_version|VARCHAR(20)|NULL|Phiên bản AI model|
|created\_at|DATETIME|NOT NULL|Thời gian tạo|
|sms\_history\_id|BIGINT|FK -> sms\_history.id|Liên kết SMS|
|call\_history\_id|BIGINT|FK -> call\_history.id|Liên kết cuộc gọi|

## **4.2. Bảng trusted\_numbers (bị thiếu)**

|**Tên trường**|**Kiểu dữ liệu**|**Ràng buộc**|**Mô tả**|
| :- | :- | :- | :- |
|id|BIGINT|PK, AUTO\_INCREMENT|Mã|
|user\_id|BIGINT|FK -> users.id|Người dùng|
|phone\_number|VARCHAR(20)|NOT NULL|Số tin cậy|
|label|VARCHAR(50)|NULL|Nhãn (Ba, Mẹ, Công ty...)|
|created\_at|DATETIME|NOT NULL|Ngày thêm|

## **4.3. Bảng user\_settings (bị thiếu)**

|**Tên trường**|**Kiểu dữ liệu**|**Ràng buộc**|**Mô tả**|
| :- | :- | :- | :- |
|id|BIGINT|PK, AUTO\_INCREMENT|Mã|
|user\_id|BIGINT|FK -> users.id UNIQUE|Người dùng|
|notification\_enabled|BOOLEAN|DEFAULT TRUE|Bật thông báo|
|auto\_analyze\_sms|BOOLEAN|DEFAULT TRUE|Tự động phân tích SMS|
|call\_analysis\_enabled|BOOLEAN|DEFAULT TRUE|Bật phân tích cuộc gọi|
|risk\_threshold\_notify|INT|DEFAULT 30|Ngưỡng thông báo|
|risk\_threshold\_overlay|INT|DEFAULT 60|Ngưỡng overlay|
|risk\_threshold\_block|INT|DEFAULT 85|Ngưỡng gợi ý block|
|offline\_mode|BOOLEAN|DEFAULT FALSE|Chế độ offline|
|updated\_at|DATETIME|NOT NULL|Cập nhật lần cuối|

## **4.4. Bổ sung quan hệ database**

|**Quan hệ**|**Kiểu**|
| :- | :- |
|users (1) — (N) trusted\_numbers|One-to-Many|
|users (1) — (1) user\_settings|One-to-One|
|sms\_history (1) — (1) ai\_logs|One-to-One|
|call\_history (1) — (1) ai\_logs|One-to-One|
|users (1) — (N) sms\_history|One-to-Many (cần thêm FK user\_id)|
|users (1) — (N) call\_history|One-to-Many (cần thêm FK user\_id)|

# **5. TÀI LIỆU TEST CASE**

## **5.1. Test SMS Detection**

|**TC ID**|**Input SMS**|**Expected Risk Score**|**Expected Result**|
| :- | :- | :- | :- |
|TC-SMS-01|"Chuyển khoản ngay 50 triệu cho tôi"|>= 80|Scam — Critical|
|TC-SMS-02|"OTP của bạn là 123456, không chia sẻ với ai"|0-30|Safe — OTP từ trusted service|
|TC-SMS-03|"Bạn đã trúng thưởng 100 triệu. Bấm vào bit.ly/abc"|>= 85|Scam — Lottery + shortened URL|
|TC-SMS-04|"Công an Hà Nội thông báo tài khoản bạn liên quan đến..."|>= 90|Scam — Fake Police Critical|
|TC-SMS-05|"Giao hàng GHTK: đơn #12345 đang trên đường"|0-25|Safe — Delivery notification|
|TC-SMS-06|"Tài khoản MBBank của bạn đăng nhập bất thường"|31-60|Suspicious — cần kiểm tra|
|TC-SMS-07|"Xin chào! Đầu tư lợi nhuận 50%/tháng, an toàn, uy tín"|>= 75|Dangerous — Investment Scam|
|TC-SMS-08|SMS từ số trong danh bạ|0 (bỏ qua)|Skip — trusted contact|

## **5.2. Test URL Detection**

|**TC ID**|**URL**|**Expected Score**|**Lý do**|
| :- | :- | :- | :- |
|TC-URL-01|bit.ly/abc123|+25|Shortened URL|
|TC-URL-02|vietcombank-security.net/login|+70|Fake bank domain|
|TC-URL-03|http://192.168.1.1/bank|+45|IP address URL + HTTP|
|TC-URL-04|https://vietcombank.com.vn|0|Whitelist — real domain|
|TC-URL-05|mbbank-support.info/verify|+65|Fake domain pattern|

## **5.3. Test Risk Score Engine**

|**TC ID**|**Input Combination**|**Keyword Score**|**URL Score**|**Blacklist**|**Total Expected**|
| :- | :- | :- | :- | :- | :- |
|TC-SCORE-01|OTP + chuyển khoản + URL rút gọn|70|25|0|>= 85|
|TC-SCORE-02|Công an + chuyển tiền + số blacklist|85|0|60|100 (clamped)|
|TC-SCORE-03|Giao hàng + link hợp lệ|10|0|0|10|
|TC-SCORE-04|Ngân hàng + tài khoản khóa + domain giả|60|70|0|100 (clamped)|

## **5.4. Test API Endpoints**

|**TC ID**|**Endpoint**|**Input**|**Expected Status**|**Expected Response**|
| :- | :- | :- | :- | :- |
|TC-API-01|POST /api/analyze/sms|SMS hợp lệ|200|risk\_score, is\_scam, category|
|TC-API-02|POST /api/analyze/sms|message rỗng|400|Validation error|
|TC-API-03|POST /api/analyze/sms|Không có JWT|401|Unauthorized|
|TC-API-04|GET /api/scam-phone/0901234567|Số trong blacklist|200|exists: true|
|TC-API-05|GET /api/scam-phone/0123456789|Số không tồn tại|200|exists: false|
|TC-API-06|POST /api/report|Report hợp lệ|201|success: true|
|TC-API-07|GET /api/blacklist|JWT hợp lệ|200|Array blacklist|
|TC-API-08|POST /api/auth/register|Phone mới|201|tokens|
|TC-API-09|POST /api/auth/register|Phone đã tồn tại|400|Error|
|TC-API-10|POST /api/auth/refresh|Refresh token hết hạn|401|Unauthorized|

# **6. ĐẶC TẢ UI/UX CÁC MÀN HÌNH**

## **6.1. HomeScreen — Dashboard**

|**Component**|**Mô tả**|**Vị trí**|
| :- | :- | :- |
|Header|Logo Scam Warning + nút Settings (icon gear)|Top|
|Status Card|Trạng thái bảo vệ: ON/OFF toggle lớn, màu xanh khi bật|Top center|
|Stats Row|3 card nhỏ: SMS hôm nay | Cuộc gọi | Scam phát hiện|Below status|
|Recent Activity|List 5 SMS/Call gần nhất với badge màu rủi ro|Middle|
|Bottom Nav|4 tab: Home | History | Report | Settings|Bottom|

## **6.2. WarningOverlay — Cảnh báo realtime**

|**Element**|**Màu sắc theo Risk Level**|**Nội dung**|
| :- | :- | :- |
|Background overlay|0-30: Xanh lá | 31-60: Vàng | 61-80: Cam | 81-100: Đỏ|Semi-transparent|
|Risk Score hiển thị|Số lớn 48sp, bold, màu tương ứng|VD: 92|
|Risk Label|AN TOÀN / NGHI NGỜ / NGUY HIỂM / LỪA ĐẢO|Bold 20sp|
|Category|Loại scam: Fake Bank, OTP Scam...|Regular 16sp|
|Reasons list|Tối đa 3 lý do phát hiện|Bullet list|
|Action buttons|"Chặn số" (đỏ) + "Bỏ qua" (xám)|Bottom|

## **6.3. HistoryScreen — Lịch sử**

|**Component**|**Mô tả**|
| :- | :- |
|Filter tabs|Tất cả | SMS | Cuộc gọi | Scam | An toàn|
|List item|Avatar (SMS/Call icon) + Số điện thoại + Thời gian + Risk Score badge|
|Risk badge màu|Xanh (<30) | Vàng (31-60) | Cam (61-80) | Đỏ (>80)|
|Detail view|Tap mở modal: nội dung đầy đủ + lý do + action block/report|

## **6.4. ReportScreen — Báo cáo cộng đồng**

|**Field**|**Type**|**Validation**|
| :- | :- | :- |
|Số điện thoại|Text input|Đúng format VN (10 số)|
|Loại lừa đảo|Dropdown: Fake Bank/Police/OTP/Investment/Job/Other|Bắt buộc|
|Mô tả|TextArea|Tối đa 500 ký tự, optional|
|Gửi báo cáo|Button primary|Disabled khi form invalid|

## **6.5. SettingsScreen — Cài đặt**

|**Setting**|**Type**|**Default**|
| :- | :- | :- |
|Bảo vệ SMS|Toggle|ON|
|Bảo vệ cuộc gọi|Toggle|ON|
|Ngưỡng thông báo|Slider 0-100|30|
|Ngưỡng overlay|Slider 0-100|60|
|Gợi ý chặn từ điểm|Slider 0-100|85|
|Chế độ offline|Toggle|OFF|
|Quản lý số tin cậy|Button -> TrustedNumbersScreen|-|
|Đồng bộ blacklist|Button (manual sync)|-|

# **7. YÊU CẦU PHI CHỨC NĂNG (NON-FUNCTIONAL REQUIREMENTS)**

## **7.1. Performance Requirements**

|**Thành phần**|**Metric**|**Target**|**Max Acceptable**|
| :- | :- | :- | :- |
|SMS Detection (online)|Response time|< 2 giây|< 5 giây|
|SMS Detection (offline)|Response time|< 500ms|< 1 giây|
|Call STT processing|Latency per chunk|< 3 giây|< 5 giây|
|Overlay hiển thị|Time to display|< 1 giây|< 2 giây|
|App startup time|Cold start|< 3 giây|< 5 giây|
|Blacklist sync|Background sync|< 30 giây|< 60 giây|
|API throughput|Requests/minute|100 req/min/user|-|
|Battery usage|Background service|< 2% mỗi giờ|< 5% mỗi giờ|

## **7.2. Compatibility Requirements**

|**Platform**|**Min Version**|**Target Version**|**Notes**|
| :- | :- | :- | :- |
|Android|Android 8.0 (API 26)|Android 14 (API 34)|SYSTEM\_ALERT\_WINDOW cần từ API 23+|
|RAM|2GB minimum|4GB recommended|Gemma-2B cần ít nhất 2GB free RAM|
|Storage|500MB free|2GB recommended|Model file ~1.5GB|
|Network|3G minimum|4G/WiFi recommended|Offline mode cho 2G/no network|

## **7.3. Security Requirements**
- HTTPS bắt buộc cho tất cả API calls
- JWT token expire sau 24 giờ, refresh token sau 30 ngày
- SMS content KHÔNG được upload lên server — chỉ metadata
- Audio recording KHÔNG lưu trên server — chỉ transcript text
- Room Database mã hóa với SQLCipher
- SharedPreferences lưu token phải dùng EncryptedSharedPreferences
- GDPR/PDPA compliant: user phải đồng ý trước khi thu thập data
- Certificate Pinning cho production API calls

## **7.4. Availability & Reliability**

|**Requirement**|**Target**|
| :- | :- |
|API uptime|99\.5% monthly|
|Offline functionality|Core features hoạt động 100% khi mất internet|
|Data loss prevention|SQLite backup tự động mỗi 24h|
|Crash rate|< 0.1% sessions|

# **8. HƯỚNG DẪN TRIỂN KHAI (DEPLOYMENT GUIDE)**

## **8.1. Backend Deployment — Docker**
\# docker-compose.yml

version: '3.8'

services:

`  `fastapi:

`    `build: .

`    `ports:

`      `- '8000:8000'

`    `environment:

`      `- DATABASE\_URL=mysql+pymysql://root:pass@mysql:3306/scam\_warning

`      `- SECRET\_KEY=your-secret-key

`    `depends\_on:

`      `- mysql

`  `mysql:

`    `image: mysql:8.0

`    `environment:

`      `MYSQL\_DATABASE: scam\_warning

`      `MYSQL\_ROOT\_PASSWORD: your-password

`    `volumes:

`      `- mysql\_data:/var/lib/mysql

volumes:

`  `mysql\_data:

## **8.2. Run Commands**
\# Development

pip install -r requirements.txt

uvicorn app.main:app --reload --host 0.0.0.0 --port 8000

\# Production

docker-compose up -d --build

\# API available at: https://api.scamwarning.com

## **8.3. Android Build**
- Min SDK: 26 (Android 8.0)
- Target SDK: 34 (Android 14)
- Build variant: debug (cho dev), release (cho production)
- Signing: keystore file bảo mật, không commit lên git
- ProGuard: bật cho release build

## **8.4. Environment Config Android**
\# local.properties (không commit)

BASE\_URL\_DEBUG=http://10.0.2.2:8000/api

BASE\_URL\_RELEASE=https://api.scamwarning.com/api

*--- Hết tài liệu bổ sung ---*
