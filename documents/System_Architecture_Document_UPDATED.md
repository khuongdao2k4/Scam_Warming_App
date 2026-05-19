**SYSTEM ARCHITECTURE DOCUMENT**

He thong Ung dung Canh bao Lua dao qua Tin nhan va Cuoc goi

*Android App | FastAPI Backend | AI Detection Engine | MySQL Cloud*
# **1. GIOI THIEU**
## **1.1. Muc dich tai lieu**
Tai lieu System Architecture mo ta toan bo kien truc he thong cua ung dung canh bao lua dao qua SMS va cuoc goi. Tai lieu nay duoc su dung de:

* Phat trien Android App, FastAPI Backend va AI Engine
* Ho tro AI code generation hieu dung toan bo he thong
* Huong dan trien khai production
* Lam co so de scale va mo rong tinh nang

## **1.2. Muc tieu he thong**

|**Muc tieu**|**Mo ta**|
| :- | :- |
|Phat hien SMS lua dao|Phan tich noi dung SMS realtime, tinh Risk Score, canh bao nguoi dung|
|Phat hien cuoc goi lua dao|STT ghi am cuoc goi, phan tich transcript, overlay canh bao trong khi goi|
|AI Detection chinh xac|Ket hop keyword rules + NLP + ML model + blacklist community|
|Bao ve nguoi dung|Canh bao truoc khi nguoi dung thuc hien giao dich, goi y chan so|
|Privacy-first|Khong luu noi dung SMS/audio, chi xu ly metadata|
|Online + Offline|Hoat dong day du ca khi mat mang qua Local AI|

# **2. KIEN TRUC TONG THE HE THONG**
He thong gom 4 thanh phan chinh hoat dong theo mo hinh Client-Server, voi AI Detection Engine la hat nhan xu ly:

|<p>**Android App (Client)**</p><p>*SMS BroadcastReceiver  |  Call PhoneStateListener  |  Overlay Warning Service*</p><p>*Local AI Engine (Gemma-2B)  |  Room SQLite  |  Speech-to-Text  |  Notification Manager*</p>|
| :- |
|*v  HTTPS REST API  v*|
|<p>**FastAPI Backend**</p><p>*Authentication Service  |  SMS Analysis API  |  Call Analysis API*</p><p>*Report Management API  |  Blacklist Sync API  |  AI Logging API*</p>|
|*v  AI Analysis Request  v*|
|<p>**AI Detection Engine**</p><p>*NLP Engine  |  Keyword Detection  |  URL Detection*</p><p>*Risk Score Engine  |  Pattern Matching  |  ML Model*</p>|
|*v  Read/Write Data  v*|
|<p>**Cloud Database (MySQL)**</p><p>*scam\_phones  |  scam\_keywords  |  user\_reports  |  ai\_logs*</p><p>*blacklist  |  scam\_urls  |  trusted\_numbers  |  user\_settings*</p>|

# **3. KIEN TRUC ANDROID APP**
Android App la thanh phan chinh hoat dong realtime tren thiet bi nguoi dung, chay Background Service lien tuc de phat hien SMS va cuoc goi lua dao.

## **3.1. Cac Module chinh**

|**Module**|**Loai**|**Chuc nang chinh**|
| :- | :- | :- |
|SmsBroadcastReceiver|BroadcastReceiver|Lang nghe SMS den, kich hoat AI Detection|
|CallDetectionService|Foreground Service|Detect cuoc goi, bat dau STT khi nguoi dung nhac may|
|OverlayWarningService|Foreground Service|Hien thi overlay canh bao noi tren man hinh|
|LocalAIEngine|Android Library|Keyword/rule detection offline, Gemma-2B inference|
|SpeechToTextModule|Android Library|Chuyen am thanh -> text (Google Cloud STT / Vosk)|
|NotificationManager|System Service|Gui thong bao canh bao, badge warning|
|Room Database|Local Storage|Luu lich su SMS, cuoc goi, blacklist cache, AI logs|
|BlacklistSyncWorker|WorkManager|Dong bo blacklist tu server moi 6 gio|

## **3.2. SMS Detection Flow**

|**1.**  SMS den -> SmsBroadcastReceiver.onReceive(phoneNumber, message)|
| :- |
|v|
|**2.**  Kiem tra so: co trong danh ba hoac trusted numbers? -> Bo qua neu la so quen|
|v|
|**3.**  Kiem tra mang: co internet? -> Online: goi FastAPI; Offline/Timeout: Local AI|
|v|
|**4.**  AI phan tich: Keyword + URL + Blacklist + NLP + ML -> Risk Score (0-100)|
|v|
|**5.**  DecisionEngine.decide(score) -> SAFE / SUSPICIOUS / DANGEROUS / SCAM|
|v|
|**6.**  Luu ket qua vao Room DB (sms\_history + ai\_logs)|
|v|
|**7.**  Score >= threshold -> NotificationService / WarningOverlay hien thi canh bao|

## **3.3. Call Detection Flow**

|**1.**  Cuoc goi den -> PhoneStateListener.RINGING -> kiem tra so la|
| :- |
|v|
|**2.**  Goi FastAPI: GET /api/scam-phone/{phone} kiem tra Caller ID trong blacklist|
|v|
|**3.**  Hien thi ket qua Caller ID check len overlay ngay khi dang do chuan|
|v|
|**4.**  Nguoi dung nhac may -> OFFHOOK -> MediaRecorder bat dau ghi am qua mic|
|v|
|**5.**  Chia am thanh thanh chunk 5 giay -> Speech-to-Text (online/offline)|
|v|
|**6.**  Transcript chunk -> AI phan tich -> cap nhat Risk Score lien tuc|
|v|
|**7.**  Score vuot nguong -> OverlayService cap nhat canh bao (vang / cam / do)|
|v|
|**8.**  Cuoc goi ket thuc -> IDLE -> luu call\_history + ai\_logs -> dong overlay|

## **3.4. Overlay Warning UI**

|**Risk Level**|**Score**|**Mau overlay**|**Noi dung hien thi**|
| :- | :- | :- | :- |
|SAFE|0-30|Khong hien thi|---|
|SUSPICIOUS|31-60|Vang nhat|So nay co dau hieu nghi ngo, can than|
|DANGEROUS|61-80|Cam|CANH BAO: So nay co dau hieu lua dao - Risk: XX%|
|SCAM|81-100|Do dam|LUA DAO! So nay rat nguy hiem - Goi y: Chan so ngay|

# **4. FASTAPI BACKEND ARCHITECTURE**
Backend su dung FastAPI (Python) voi kien truc async, xu ly hang loat request dong thoi, tich hop AI Detection Engine va MySQL database.

## **4.1. API Endpoints tong quat**

|**Nhom API**|**Endpoint**|**Chuc nang**|
| :- | :- | :- |
|Auth|/api/auth/register, /login, /refresh, /logout|Xac thuc nguoi dung, quan ly JWT token|
|SMS Analysis|/api/analyze/sms|Nhan SMS tu Android, phan tich AI, tra Risk Score|
|Call Analysis|/api/analyze/call|Nhan transcript chunk, phan tich, tra ket qua realtime|
|Scam Phone|/api/scam-phone/{phone}|Kiem tra so trong blacklist cong dong|
|Blacklist|/api/blacklist|Lay danh sach blacklist, dong bo xuong Android|
|Report|/api/report|Nguoi dung bao cao so/SMS lua dao len cong dong|
|User|/api/user/settings, /trusted-numbers|Quan ly cai dat va so tin cay|

## **4.2. Xu ly SMS tren FastAPI**

|**1.**  Nhan POST /api/analyze/sms (JWT auth required)|
| :- |
|v|
|**2.**  Tien xu ly: lowercase, remove special chars, extract URLs|
|v|
|**3.**  Chay song song: Keyword Engine + URL Engine + Blacklist Engine|
|v|
|**4.**  Chay NLP Analysis (Python NLP + rule-based)|
|v|
|**5.**  Tong hop ket qua -> Risk Score Engine tinh tong diem|
|v|
|**6.**  DecisionEngine.decide(score) -> tra ve AnalysisResult|
|v|
|**7.**  Luu ai\_log vao MySQL (metadata, khong luu noi dung SMS)|
|v|
|**8.**  Tra ve JSON response voi risk\_score, is\_scam, category, reasons|

## **4.3. Cau truc thu muc FastAPI**

|<p>**Project Structure**</p><p>scam-warning-backend/</p><p>|-- app/</p><p>|   |-- main.py            # FastAPI app init, CORS, routers</p><p>|   |-- config.py          # Settings, env variables</p><p>|   |-- database.py        # MySQL connection (SQLAlchemy)</p><p>|   |-- models/            # SQLAlchemy ORM models</p><p>|   |-- schemas/           # Pydantic request/response DTOs</p><p>|   |-- routers/           # Route handlers (auth, sms, call, ...)</p><p>|   |-- services/          # Business logic (ai\_service, risk\_score...)</p><p>|   |-- middleware/        # JWT auth, rate limiter</p><p>|   |-- utils/             # phone\_utils, jwt\_utils</p><p>|-- requirements.txt</p><p>|-- Dockerfile</p><p>|-- docker-compose.yml</p>|
| :- |

# **5. AI DETECTION ENGINE**
AI Engine la thanh phan cot loi xu ly logic phat hien lua dao. Chay tren FastAPI backend (online) va Android device (offline).

## **5.1. Pipeline phan tich AI**

|**1.**  Input: SMS text hoac Call Transcript|
| :- |
|v|
|**2.**  Preprocessing: chuyen thuong, xoa ky tu dac biet, tach URL|
|v|
|**3.**  Keyword Detection: do tu khoa nguy hiem -> Keyword Score|
|v|
|**4.**  Blacklist Check: kiem tra so/URL trong blacklist -> Blacklist Score|
|v|
|**5.**  URL Analysis: phat hien URL rut gon, domain gia, IP -> URL Score|
|v|
|**6.**  Pattern Matching: so khop mau scam da biet -> Pattern Score|
|v|
|**7.**  NLP Analysis: phan tich ngu nghia, hanh vi tham tung tam ly -> NLP Score|
|v|
|**8.**  Risk Score Engine: tong hop tat ca diem -> clamp ve 0-100|
|v|
|**9.**  Decision Engine: quyet dinh muc canh bao va hanh dong|

## **5.2. Cong thuc Risk Score**

|<p>**Risk Score Formula**</p><p>Risk Score = Keyword Score</p><p>`           `+ URL Score</p><p>`           `+ Blacklist Score</p><p>`           `+ Pattern Score</p><p>`           `+ NLP Score</p><p>`           `+ ML Confidence</p><p></p><p>Clamp: if Risk Score > 100 => set 100</p>|
| :- |

## **5.3. Risk Level va Action**

|**Score**|**Level**|**Mau**|**Action**|
| :- | :- | :- | :- |
|**0-30**|**SAFE**|Xanh la|Khong canh bao|
|**31-60**|**SUSPICIOUS**|Vang|Notification nhe|
|**61-80**|**DANGEROUS**|Cam|Overlay canh bao|
|**81-100**|**SCAM**|Do|Overlay do + goi y chan so|

# **6. DATABASE ARCHITECTURE**
## **6.1. Hybrid Storage Strategy**
He thong su dung chien luoc Hybrid Storage: luu tru cu bo tren thiet bi va dam may theo tung loai du lieu de dam bao ca Privacy lan Community Intelligence:

|**Du lieu**|**Local SQLite (Android)**|**Cloud MySQL**|**Ly do**|
| :- | :- | :- | :- |
|SMS history|Luu day du|Khong sync|Privacy: noi dung SMS khong roi thiet bi|
|Call history|Luu day du|Khong sync|Privacy: transcript khong len server|
|Blacklist|Cache local|Master data|Tra cuu nhanh offline, dong bo tu server|
|AI logs|Luu day du|Sync metadata|Metadata giup cong dong canh bao nhau|
|Trusted numbers|Luu local|Sync|Dung duoc tren nhieu thiet bi|
|User settings|Luu local|Sync|Dong bo khi doi thiet bi|

## **6.2. Bang du lieu chinh (MySQL Cloud)**

|**Ten bang**|**Mo ta**|**Du lieu quan trong**|
| :- | :- | :- |
|users|Tai khoan nguoi dung|id, phone\_number, device\_id, created\_at|
|scam\_phones|Blacklist so dien thoai|phone\_number, risk\_level, report\_count|
|scam\_keywords|Tu khoa lua dao|keyword, score, category, language|
|scam\_urls|URL va domain den|url, domain, type, report\_count|
|user\_reports|Bao cao cong dong|phone\_number, category, description|
|ai\_logs|Log ket qua AI|source\_type, risk\_score, category, processing\_mode|
|trusted\_numbers|So tin cay cua user|user\_id, phone\_number, label|
|user\_settings|Cai dat ca nhan|user\_id, thresholds, feature\_flags|

# **7. DATA FLOW ARCHITECTURE**
## **7.1. SMS Analysis Data Flow (Online)**

|**1.**  SMS den thiet bi Android|
| :- |
|v|
|**2.**  SmsBroadcastReceiver bat SMS, kiem tra danh ba|
|v|
|**3.**  Gui POST /api/analyze/sms (JWT + phone + message) len FastAPI|
|v|
|**4.**  FastAPI chay AI Pipeline: Keyword + URL + Blacklist + NLP|
|v|
|**5.**  Tra ve AnalysisResult (risk\_score, is\_scam, category, reasons)|
|v|
|**6.**  Android luu vao Room DB, hien thi overlay neu score >= threshold|

## **7.2. SMS Analysis Data Flow (Offline)**

|**1.**  SMS den - thiet bi mat mang hoac API timeout (>5s)|
| :- |
|v|
|**2.**  Chuyen sang LocalAIEngine.analyze(phone, message)|
|v|
|**3.**  Chay Keyword Detection + URL Detection + Blacklist cache (local)|
|v|
|**4.**  Gemma-2B offline inference cho NLP analysis (neu co du RAM)|
|v|
|**5.**  Tra ve AnalysisResult voi processingMode = OFFLINE|
|v|
|**6.**  Luu vao Room DB voi flag offline, dong bo len cloud khi co mang lai|

## **7.3. Call Analysis Data Flow**

|**1.**  Cuoc goi den -> PhoneStateListener.RINGING|
| :- |
|v|
|**2.**  Kiem tra danh ba -> neu so la: kich hoat giam sat|
|v|
|**3.**  Caller ID check: GET /api/scam-phone/{phone} (realtime)|
|v|
|**4.**  Nguoi dung nhac may -> MediaRecorder bat dau ghi qua mic|
|v|
|**5.**  Chunk 5s -> STT (Google Cloud hoac Vosk offline) -> Transcript|
|v|
|**6.**  POST /api/analyze/call tung chunk -> Risk Score cap nhat real-time|
|v|
|**7.**  OverlayService cap nhat canh bao theo score (vang -> cam -> do)|
|v|
|**8.**  Cuoc goi ket thuc -> luu call\_history + ai\_log, dong overlay|

# **8. OFFLINE & ONLINE MODE**

|**Mode**|**Kich hoat khi**|**Tinh nang co san**|**Do chinh xac**|
| :- | :- | :- | :- |
|Online (uu tien)|Co mang, API phan hoi < 5s|100% tinh nang, ML cloud, NLP day du|Cao nhat (~92%)|
|Offline (fallback)|Mat mang hoac API timeout|Keyword + URL + Blacklist local + Gemma-2B|Trung binh (~75%)|
|Degraded offline|Khong du RAM cho Gemma-2B|Chi Keyword + URL + Blacklist local|Co ban (~60%)|

# **9. SECURITY ARCHITECTURE**

|**Thanh phan**|**Giai phap bao mat**|**Mo ta chi tiet**|
| :- | :- | :- |
|Transport|HTTPS/TLS 1.2+|Toan bo API dung HTTPS, Certificate Pinning trong production|
|Auth|JWT (Access 24h + Refresh 30 ngay)|Auto refresh qua OkHttp Interceptor|
|Local DB|Room + SQLCipher AES-256|Ma hoa toan bo du lieu local tren thiet bi|
|Token|EncryptedSharedPreferences|Android Keystore-backed AES-256 encryption|
|SMS Privacy|Khong upload noi dung|Chi gui metadata va ket qua risk score|
|Audio Privacy|Khong luu file am thanh|Xu ly trong memory, khong ghi ra disk|
|Permissions|Runtime permission theo yeu cau|Xin tung permission dung luc, co fallback ro rang|

# **10. AI PIPELINE TONG HOP**

|**Buoc**|**Module**|**Diem dong gop**|**Thoi gian (ms)**|
| :- | :- | :- | :- |
|1\. Preprocessing|TextNormalizer|0 (chuan bi)|< 10ms|
|2\. Keyword Detection|KeywordEngine|0-85|< 50ms|
|3\. Blacklist Check|BlacklistEngine|0-80|< 30ms|
|4\. URL Analysis|UrlEngine|0-95|< 100ms|
|5\. Pattern Matching|PatternEngine|0-60|< 50ms|
|6\. NLP Analysis|NlpEngine (Python/Gemma)|0-30|200-500ms|
|7\. Risk Score|RiskScoreEngine|Tong hop, clamp 0-100|< 5ms|
|8\. Decision|DecisionEngine|SAFE/SUSPICIOUS/DANGEROUS/SCAM|< 5ms|

# **11. DEPLOYMENT ARCHITECTURE**
## **11.1. Mo hinh trien khai**

|**1.**  Android Device -- HTTPS REST API --> Nginx Reverse Proxy|
| :- |
|v|
|**2.**  Nginx --> FastAPI Application Server (uvicorn/gunicorn)|
|v|
|**3.**  FastAPI --> AI Detection Engine (Python services)|
|v|
|**4.**  FastAPI --> MySQL Database (RDS hoac self-hosted)|
|v|
|**5.**  WorkManager --> Blacklist Sync (background, moi 6h)|

## **11.2. Infrastructure**

|**Thanh phan**|**Dev Environment**|**Production**|
| :- | :- | :- |
|Backend|uvicorn --reload port 8000|Docker + Nginx reverse proxy|
|Database|MySQL local / Docker|MySQL 8.0 voi backup tu dong|
|AI Model|Local file|Model file trong Docker image|
|SSL|Khong (HTTP)|Let's Encrypt / AWS ACM|
|Monitoring|Log console|Logging + health check endpoint|

## **11.3. Docker Compose (Production)**

|<p>**docker-compose.yml**</p><p>version: '3.8'</p><p>services:</p><p>`  `nginx:</p><p>`    `image: nginx:alpine</p><p>`    `ports: ['443:443', '80:80']</p><p>`    `depends\_on: [fastapi]</p><p>`  `fastapi:</p><p>`    `build: .</p><p>`    `environment:</p><p>`      `- DATABASE\_URL=mysql://user:pass@mysql/scam\_warning</p><p>`      `- SECRET\_KEY=${SECRET\_KEY}</p><p>`    `depends\_on: [mysql]</p><p>`  `mysql:</p><p>`    `image: mysql:8.0</p><p>`    `volumes: [mysql\_data:/var/lib/mysql]</p>|
| :- |

# **12. SCALABILITY & MO RONG**

|**Huong mo rong**|**Mo ta**|**Buoc thuc hien**|
| :- | :- | :- |
|Scale Backend|Tang so luong FastAPI instance|Load balancer (Nginx) + stateless design|
|Scale Database|Read replicas cho MySQL|Tach read/write, cache Redis cho blacklist|
|Mo rong AI|Them model ML moi|Plugin architecture cho AI Engine|
|Mo rong Blacklist|Them nguon data moi|Import tu VirusTotal, APWG, MXToolbox|
|Phat trien iOS|Port sang iOS|Chia se Backend + AI Engine, chi doi UI|
|Mo rong ngon ngu|Them tieng Anh, tieng Hoa|Multi-language keyword list + model|

# **13. KET LUAN**
Kien truc he thong hien tai dap ung day du cac yeu cau:

* Realtime scam detection cho ca SMS va cuoc goi
* AI online/offline -- luon san sang bao ve nguoi dung du co hay khong co mang
* Privacy-first -- noi dung nhat cam khong roi khoi thiet bi nguoi dung
* Community protection -- metadata lua dao duoc dong bo de bao ve ca cong dong
* Scalable -- kien truc san sang mo rong khi can

*-- Het tai lieu --*
