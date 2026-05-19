**ANDROID TECHNICAL ARCHITECTURE DOCUMENT**

Hệ thống Ứng dụng Cảnh báo Lừa đảo qua Tin nhắn và Cuộc gọi

*Kotlin · Jetpack Compose · MVVM · Hilt · Room · Coroutine*
# **1. GIỚI THIỆU**
## **1.1. Mục đích tài liệu**
Tài liệu này mô tả kiến trúc kỹ thuật đầy đủ của Android App Scam Warning, bao gồm module architecture, MVVM structure, background service, repository pattern, realtime processing và dependency injection.

Mục tiêu:

- Giúp AI code Android đúng cấu trúc, tránh anti-pattern
- Giảm bug realtime detection bằng cấu trúc rõ ràng
- Dễ mở rộng tính năng và bảo trì
- Tối ưu maintainability với Clean Architecture

## **1.2. Công nghệ sử dụng**

|**Thành phần**|**Công nghệ**|**Phiên bản / Ghi chú**|
| :- | :- | :- |
|Ngôn ngữ|Kotlin|100% Kotlin — không dùng Java|
|Kiến trúc|MVVM + Clean Architecture|Presentation / Domain / Data layers|
|UI Framework|Jetpack Compose|Declarative UI, không dùng XML layout|
|Dependency Injection|Hilt (Dagger)|@HiltViewModel, @Inject, @Module|
|Local Database|Room + SQLCipher|Mã hóa AES-256 cho dữ liệu nhạy cảm|
|API Client|Retrofit + OkHttp|OkHttp Interceptor cho JWT auto-refresh|
|Async Processing|Kotlin Coroutine + Flow|viewModelScope, lifecycleScope|
|Background Task|WorkManager|Blacklist sync định kỳ|
|Realtime Service|Foreground Service|Call detection + STT processing|
|JSON|Moshi|Type-safe JSON parsing|
|Navigation|Navigation Component|Single Activity + Compose NavHost|
|Local AI|Gemma-2B via TFLite|Offline fallback AI model|
|Token Storage|EncryptedSharedPreferences|Android Jetpack Security|

# **2. KIẾN TRÚC TỔNG THỂ — CLEAN ARCHITECTURE**
Ứng dụng tuân theo Clean Architecture với 4 layer phân tầng rõ ràng, dependency chỉ đi từ ngoài vào trong:

|<p>**📱  PRESENTATION LAYER**</p><p>*Jetpack Compose UI  |  Activities  |  ViewModels  |  StateFlow / LiveData*</p>|
| :- |
|▼|
|<p>**🧠  DOMAIN LAYER**</p><p>*Use Cases  |  Business Logic  |  AI Detection Rules  |  Decision Engine*</p>|
|▼|
|<p>**💾  DATA LAYER**</p><p>*Repository Pattern  |  Retrofit API  |  Room Database  |  Local AI Engine  |  SharedPreferences*</p>|
|▼|
|<p>**🔧  ANDROID SYSTEM LAYER**</p><p>*SMS BroadcastReceiver  |  PhoneStateListener  |  Overlay Foreground Service  |  WorkManager  |  Notification Manager*</p>|

# **3. PRESENTATION LAYER**
## **3.1. Màn hình (Screens)**

|**Screen**|**Mục đích**|**ViewModel**|
| :- | :- | :- |
|SplashScreen|Khởi động, kiểm tra auth, chuyển hướng|SplashViewModel|
|PermissionScreen|Hướng dẫn và xin permission theo thứ tự|PermissionViewModel|
|HomeScreen|Dashboard: status, stats, recent activity|HomeViewModel|
|WarningOverlay|Cảnh báo realtime nổi trên màn hình|WarningViewModel|
|HistoryScreen|Lịch sử SMS/Call đã phân tích, filter|HistoryViewModel|
|ReportScreen|Báo cáo số lừa đảo lên cộng đồng|ReportViewModel|
|SettingsScreen|Cài đặt ngưỡng, permissions, trusted numbers|SettingsViewModel|

## **3.2. StateFlow Architecture — Quản lý UI State**
Mỗi ViewModel expose một UiState duy nhất qua StateFlow, Compose UI chỉ render theo state này:

|<p>**WarningViewModel.kt**</p><p>data class WarningUiState(</p><p>`    `val isLoading: Boolean = false,</p><p>`    `val riskScore: Int = 0,</p><p>`    `val riskLevel: RiskLevel = RiskLevel.SAFE,</p><p>`    `val category: String? = null,</p><p>`    `val reasons: List<String> = emptyList(),</p><p>`    `val phoneNumber: String = "",</p><p>`    `val isOfflineMode: Boolean = false</p><p>)</p>|
| :- |

## **3.3. ViewModel States**

|**State**|**Mô tả**|**UI hiển thị**|
| :- | :- | :- |
|Idle|Không có hoạt động|Không hiển thị gì|
|Loading|Đang gửi lên server phân tích|Loading spinner|
|Analyzing|Đang xử lý AI local|Progress indicator|
|Warning|Có kết quả cảnh báo|Overlay / Notification|
|Blocked|Người dùng đã chặn số|Thông báo đã chặn|
|Offline|Đang dùng Local AI|Badge "Offline mode"|

# **4. DOMAIN LAYER — BUSINESS LOGIC**
## **4.1. Use Cases**

|**Use Case**|**Chức năng**|**Input → Output**|
| :- | :- | :- |
|AnalyzeSmsUseCase|Điều phối phân tích SMS|SmsData → AnalysisResult|
|AnalyzeCallUseCase|Điều phối phân tích cuộc gọi|TranscriptChunk → AnalysisResult|
|BlockNumberUseCase|Chặn số điện thoại trên Android|PhoneNumber → Boolean|
|SyncBlacklistUseCase|Đồng bộ blacklist từ server|() → SyncResult|
|ReportScamUseCase|Gửi report lên server cộng đồng|ReportData → Boolean|
|GetHistoryUseCase|Lấy lịch sử phân tích|Filter → Flow<List<HistoryItem>>|

## **4.2. Decision Engine**
Decision Engine trong Domain Layer nhận Risk Score từ AI và quyết định action:

|<p>**DecisionEngine.kt**</p><p>fun decide(score: Int): Decision = when {</p><p>`    `score >= 85 -> Decision.SCAM\_WITH\_BLOCK\_SUGGESTION</p><p>`    `score >= 80 -> Decision.SCAM</p><p>`    `score >= 61 -> Decision.DANGEROUS</p><p>`    `score >= 31 -> Decision.SUSPICIOUS</p><p>`    `else        -> Decision.SAFE</p><p>}</p>|
| :- |

# **5. DATA LAYER — REPOSITORY PATTERN**
## **5.1. Repository Architecture**
Repository là trung gian duy nhất giữa Domain Layer và Data Sources. ViewModel không bao giờ truy cập trực tiếp vào Room DB hay Retrofit:

|**1.**  ViewModel gọi Use Case|
| :- |
|v|
|**2.**  Use Case gọi Repository interface (Domain Layer)|
|v|
|**3.**  Repository Implementation quyết định dùng Local hay Remote|
|v|
|**4.**  Local: Room DB (SQLite)  |  Remote: Retrofit → FastAPI|
|v|
|**5.**  Kết quả trả về Use Case → ViewModel → UI|

## **5.2. Repository List**

|**Repository**|**Interface**|**Chức năng**|
| :- | :- | :- |
|SmsRepository|ISmsRepository|Lưu/đọc SMS history, kết quả phân tích|
|CallRepository|ICallRepository|Lưu/đọc call history, transcript|
|BlacklistRepository|IBlacklistRepository|Local blacklist cache + server sync|
|AiRepository|IAiRepository|Gọi AI phân tích SMS/Call (online + offline)|
|ReportRepository|IReportRepository|Gửi report và lấy lịch sử report|
|UserRepository|IUserRepository|Auth, settings, trusted numbers|

## **5.3. Room Database Tables**

|**Table**|**Loại lưu trữ**|**Mô tả**|
| :- | :- | :- |
|sms\_history|Local only|Lịch sử SMS đã phân tích (không sync lên cloud)|
|call\_history|Local only|Lịch sử cuộc gọi (không sync lên cloud)|
|blacklist|Local cache|Cache blacklist từ server, sync mỗi 6 giờ|
|ai\_logs|Local + Cloud sync|Log kết quả AI, sync metadata lên cloud|
|trusted\_numbers|Local + Cloud sync|Số tin cậy, sync để dùng đa thiết bị|
|user\_settings|Local + Cloud sync|Cài đặt người dùng, sync đa thiết bị|

## **5.4. Network Layer — Retrofit + OkHttp**

|<p>**Cấu hình OkHttp Interceptors**</p><p>OkHttpClient.Builder()</p><p>    .addInterceptor(AuthInterceptor())   // Tự động thêm JWT header</p><p>    .addInterceptor(RetryInterceptor())  // Retry khi 401 (auto refresh token)</p><p>    .addInterceptor(LoggingInterceptor()) // Log request/response (debug only)</p><p>    .connectTimeout(10, TimeUnit.SECONDS)</p><p>    .readTimeout(30, TimeUnit.SECONDS)</p><p>    .build()</p>|
| :- |

# **6. BACKGROUND SERVICE ARCHITECTURE**
## **6.1. Tổng quan các Service**

|**Service**|**Loại**|**Trigger**|**Vai trò**|
| :- | :- | :- | :- |
|SmsBroadcastReceiver|BroadcastReceiver|SMS\_RECEIVED intent|Nhận SMS → kích hoạt phân tích|
|CallDetectionService|Foreground Service|PHONE\_STATE\_CHANGED|Detect cuộc gọi → STT → AI|
|OverlayService|Foreground Service|Gọi từ CallDetectionService|Hiển thị overlay realtime|
|BlacklistSyncWorker|WorkManager|Mỗi 6 giờ, khi có mạng|Đồng bộ blacklist từ server|
|AiLogSyncWorker|WorkManager|Mỗi 1 giờ, khi có mạng|Sync AI log metadata lên cloud|

## **6.2. SMS Detection Flow**

|**1.**  SMS đến → SmsBroadcastReceiver.onReceive()|
| :- |
|v|
|**2.**  Kiểm tra số: có trong danh bạ hoặc trusted numbers? → Nếu có: bỏ qua|
|v|
|**3.**  Tạo SmsAnalysisService.analyze(phone, message)|
|v|
|**4.**  Kiểm tra mạng: NetworkMonitor.isAvailable()|
|v|
|**5.**  Online: gọi ApiService.analyzeSms() → FastAPI Backend|
|v|
|**6.**  Offline / Timeout: gọi LocalAIEngine.analyze()|
|v|
|**7.**  Nhận AnalysisResult → DecisionEngine.decide(riskScore)|
|v|
|**8.**  Lưu vào Room DB (sms\_history + ai\_logs)|
|v|
|**9.**  riskScore >= threshold → NotificationService / OverlayService|

## **6.3. Call Detection Flow**

|**1.**  Cuộc gọi đến → PhoneStateListener.RINGING|
| :- |
|v|
|**2.**  Kiểm tra số lạ (không có trong danh bạ)|
|v|
|**3.**  CallDetectionService kích hoạt → kiểm tra Caller ID trên server|
|v|
|**4.**  User nhấc máy → OFFHOOK → bắt đầu ghi âm qua MediaRecorder|
|v|
|**5.**  Chunk 5 giây → Speech-to-Text (Google Cloud STT hoặc Vosk offline)|
|v|
|**6.**  Transcript chunk → AI phân tích → Risk Score cập nhật|
|v|
|**7.**  Score vượt ngưỡng → OverlayService.showWarning()|
|v|
|**8.**  Cuộc gọi kết thúc → IDLE → lưu call\_history + ai\_logs|

# **7. ASYNC ARCHITECTURE — KOTLIN COROUTINE**
## **7.1. Coroutine Dispatcher Usage**

|**Dispatcher**|**Vai trò**|**Dùng cho**|
| :- | :- | :- |
|Dispatchers.IO|I/O operations — thread pool lớn|Room DB, Retrofit API, File I/O|
|Dispatchers.Main|Main thread — UI update|StateFlow update, Navigation|
|Dispatchers.Default|CPU-intensive — thread pool CPU|AI calculation, Risk Score Engine|

## **7.2. Coroutine Scope Strategy**

|**Scope**|**Vòng đời**|**Dùng cho**|
| :- | :- | :- |
|viewModelScope|Tự cancel khi ViewModel clear|Tất cả coroutine trong ViewModel|
|lifecycleScope|Tự cancel khi Activity/Fragment destroy|UI-bound operations|
|ServiceScope (custom)|Tự cancel khi Service destroy|Background service operations|
|GlobalScope (tránh dùng)|Không tự cancel — nguy hiểm|Không dùng trong production|

# **8. SPEECH-TO-TEXT (STT) ARCHITECTURE**
## **8.1. Chiến lược Online/Offline**

|**Chế độ**|**Engine**|**Điều kiện**|**Độ chính xác**|
| :- | :- | :- | :- |
|Online|Google Cloud Speech-to-Text API|Có mạng + có API key|Cao (~95%)|
|Offline|Vosk (local model tiếng Việt)|Mất mạng hoặc timeout|Trung bình (~75%)|

## **8.2. Audio Processing Pipeline**

|**1.**  MediaRecorder bắt đầu ghi âm (SOURCE: MIC, FORMAT: AAC)|
| :- |
|v|
|**2.**  Chia thành chunk mỗi ~5 giây|
|v|
|**3.**  Encode sang định dạng phù hợp (FLAC cho Cloud STT, PCM cho Vosk)|
|v|
|**4.**  Gửi chunk lên STT Engine (online hoặc offline)|
|v|
|**5.**  Nhận transcript text|
|v|
|**6.**  Gửi transcript chunk tới AI Analysis Engine|

# **9. AI INTEGRATION ARCHITECTURE**
## **9.1. Local AI — Gemma-2B (Offline)**

|**Thông số**|**Giá trị**|**Ghi chú**|
| :- | :- | :- |
|Model|Gemma-2B IT Quantized 4-bit|Giảm kích thước từ 5GB xuống ~1.5GB|
|Format|GGUF hoặc TFLite|TFLite ưu tiên hơn cho Android|
|RAM yêu cầu|Min 2GB free RAM|Thiết bị < 2GB RAM: tắt Local AI|
|Lưu trữ|~1.5GB trong internal storage|Tải về lần đầu, không tải lại|
|Load time|3–8 giây (lần đầu)|Cache model trong memory sau khi load|
|Inference time|200–500ms/request|Sau khi model đã được load|

## **9.2. Remote AI — FastAPI Backend**

|**Endpoint**|**Mô tả**|**Timeout**|
| :- | :- | :- |
|POST /api/analyze/sms|Phân tích nội dung SMS|5 giây|
|POST /api/analyze/call|Phân tích transcript cuộc gọi|5 giây|
|GET /api/scam-phone/{phone}|Kiểm tra số trong blacklist|3 giây|
|GET /api/blacklist|Lấy toàn bộ blacklist (sync)|30 giây|

# **10. ERROR HANDLING ARCHITECTURE**

|**Loại lỗi**|**Xử lý**|**Fallback**|
| :- | :- | :- |
|API Timeout (>5s)|Catch TimeoutException → Log warning|Chuyển sang Local AI tự động|
|No Internet|Phát hiện qua NetworkMonitor|Local AI + thông báo offline mode|
|STT Fail|Catch SpeechException → Log|Chỉ dùng Caller ID detection|
|JWT Expired (401)|AuthInterceptor tự refresh token|Nếu refresh cũng fail → force logout|
|Room DB Error|Try-catch → Log Crashlytics|Thông báo lỗi, không crash app|
|Local AI OOM|Catch OutOfMemoryError|Tắt Local AI, chỉ dùng rule-based detection|

# **11. SECURITY ARCHITECTURE**

|**Thành phần**|**Giải pháp**|**Chi tiết**|
| :- | :- | :- |
|API Security|HTTPS + Certificate Pinning|TLS 1.2+, SSL pinning trong production|
|Token Storage|EncryptedSharedPreferences|AES-256 encryption, Android Keystore|
|Local DB|Room + SQLCipher|AES-256 database encryption|
|JWT|Access 24h + Refresh 30 ngày|Auto refresh qua OkHttp Interceptor|
|Audio|Không lưu file gốc|Xử lý trong memory, không ghi ra disk|
|SMS|Không upload nội dung|Chỉ gửi metadata và risk score|
|ProGuard|Bật trong release build|Obfuscation + resource shrinking|

# **12. MODULE STRUCTURE — CẤU TRÚC PACKAGE**

|**Package / Module**|**Nội dung chính**|
| :- | :- |
|**com.scamwarning.app**|Application class, DI setup, global config|
|**├── presentation**|Activities, Compose Screens, ViewModels, UI state|
|**│   ├── home**|HomeScreen, HomeViewModel|
|**│   ├── warning**|WarningOverlay, WarningViewModel|
|**│   ├── history**|HistoryScreen, HistoryViewModel|
|**│   ├── report**|ReportScreen, ReportViewModel|
|**│   └── settings**|SettingsScreen, SettingsViewModel|
|**├── domain**|Use Cases, Repository Interfaces, Domain Models|
|**│   ├── usecase**|AnalyzeSmsUseCase, BlockNumberUseCase, ...|
|**│   ├── model**|AnalysisResult, RiskLevel, Decision, ...|
|**│   └── repository**|ISmsRepository, IBlacklistRepository, ...|
|**├── data**|Repository Implementations, Data Sources|
|**│   ├── local**|Room Database, DAOs, Entities|
|**│   ├── remote**|Retrofit Services, API DTOs|
|**│   └── repository**|SmsRepositoryImpl, BlacklistRepositoryImpl, ...|
|**├── di**|Hilt Modules: NetworkModule, DatabaseModule, ...|
|**├── service**|CallDetectionService, OverlayService, WorkerClasses|
|**├── receiver**|SmsBroadcastReceiver, BootReceiver|
|**├── ai**|LocalAIEngine, Gemma2BEngine, RiskScoreEngine|
|**├── utils**|Extensions, NetworkMonitor, PermissionManager|
|**└── ui**|Theme, Colors, Typography, Reusable Composables|

# **13. PERFORMANCE OPTIMIZATION**

|**Thành phần**|**Kỹ thuật tối ưu**|**Mục tiêu**|
| :- | :- | :- |
|Room DB|Index trên phone\_number, created\_at|Query < 50ms|
|Retrofit|Connection Pool, HTTP/2|Tái sử dụng kết nối|
|Coroutine|Structured Concurrency, proper cancellation|Không memory leak|
|Local AI Model|Load once, cache in memory|Chỉ load 1 lần khi khởi động service|
|Blacklist|In-memory cache (HashMap)|Lookup O(1) thay vì O(n) DB query|
|Overlay UI|Lightweight Compose, minimal recomposition|Hiển thị < 1 giây|
|Battery|WorkManager với constraints (charging/wifi)|< 2% battery/giờ|

# **14. KẾT LUẬN**
Android Technical Architecture được thiết kế theo chuẩn Clean Architecture với MVVM, đảm bảo:

- Realtime SMS và Call detection với latency thấp
- Hybrid AI: online (accuracy cao) và offline (always available)
- Security-first: mã hóa dữ liệu local, không lưu nội dung nhạy cảm
- Maintainable: phân tầng rõ ràng, dễ test từng layer độc lập
- Scalable: dễ thêm tính năng mới mà không ảnh hưởng code hiện tại

*— Hết tài liệu —*
