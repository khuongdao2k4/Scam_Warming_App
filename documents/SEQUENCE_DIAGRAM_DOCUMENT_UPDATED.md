**SEQUENCE DIAGRAM DOCUMENT**

**Hệ thống ứng dụng cảnh báo lừa đảo qua tin nhắn và cuộc gọi**

-----
**1. GIỚI THIỆU**

**1.1. Mục đích tài liệu**

Tài liệu Sequence Diagram mô tả:

- trình tự xử lý dữ liệu, 
- luồng giao tiếp giữa các thành phần hệ thống, 
- realtime processing, 
- async processing, 
- background service flow, 
- AI Detection flow. 

Mục tiêu:

- giúp AI hiểu chính xác flow xử lý, 
- hỗ trợ code Android realtime, 
- hỗ trợ FastAPI backend, 
- giảm lỗi threading và concurrency. 
-----
**1.2. Thành phần tham gia trong hệ thống**

|**Thành phần**|**Vai trò**|
| :-: | :-: |
|User|Người dùng ứng dụng|
|SMS Listener|Lắng nghe SMS|
|Call Listener|Lắng nghe cuộc gọi|
|Overlay Service|Hiển thị cảnh báo|
|Notification Manager|Hiển thị thông báo|
|Speech-to-Text Engine|Chuyển giọng nói thành văn bản|
|Local AI Engine|AI offline|
|FastAPI Backend|API xử lý|
|AI Detection Engine|AI phân tích|
|SQLite Database|Lưu local|
|MySQL Database|Lưu cloud|

-----
**2. SEQUENCE DIAGRAM PHÂN TÍCH SMS**

**2.1. Flow tổng quát**

**Mục tiêu**

Mô tả quá trình:

- SMS đến, 
- kiểm tra danh bạ, 
- hỏi người dùng, 
- AI phân tích, 
- hiển thị cảnh báo, 
- đề xuất block số. 
-----
**2.2. Sequence Diagram**

User\
` `|\
` `| Nhận SMS\
` `v\
Android System\
` `|\
` `v\
SMS Listener\
` `|\
` `| Kiểm tra số điện thoại\
` `v\
Contacts Manager\
` `|\
` `| Số nằm trong danh bạ?\
` `+------------------------------+\
` `| YES                          | NO\
` `|                              |\
` `| Bỏ qua phân tích             |\
` `|                              v\
` `|                       Notification Manager\
` `|                              |\
` `|                    Hiển thị popup xác nhận\
` `|                              |\
` `|                "Bạn có muốn kiểm tra SMS?"\
` `|                              |\
` `|<----------- User ------------|\
` `|                              |\
` `| Người dùng đồng ý            |\
` `|                              v\
` `|                       Local AI Engine\
` `|                              |\
` `|                 Keyword Detection\
` `|                              |\
` `|                 Blacklist Check\
` `|                              |\
` `|                 URL Detection\
` `|                              |\
` `|                Có internet?\
` `|                    |\
` `|          +---------+---------+\
` `|          | YES               | NO\
` `|          |                   |\
` `|          v                   v\
` `|    FastAPI Backend     Local AI Result\
` `|          |\
` `|          v\
` `|    AI Detection Engine\
` `|          |\
` `|    Risk Score Engine\
` `|          |\
` `|    Generate Result\
` `|          |\
` `|          v\
` `|    FastAPI Response\
` `|          |\
` `|          v\
` `|    Overlay Service\
` `|          |\
` `| Hiển thị cảnh báo realtime\
` `|          |\
` `| Hiển thị:\
` `| - Risk Score\
` `| - Scam Category\
` `| - Recommendation\
` `|          |\
` `| "Bạn có muốn block số này?"\
` `|          |\
` `|<--------- User -------------->\
` `|          |\
` `| Đồng ý block\
` `|          |\
` `|          v\
` `|    Android Block Manager\
` `|          |\
` `|    Block Number\
` `|          |\
` `|          v\
` `|    SQLite Database\
` `|          |\
` `| Lưu lịch sử phân tích

-----
**2.3. Giải thích chi tiết flow SMS**

-----
**Bước 1 — SMS đến**

Android System gửi broadcast:

SMS\_RECEIVED

SMS Listener nhận sự kiện.

-----
**Bước 2 — Kiểm tra danh bạ**

Hệ thống kiểm tra:

- số điện thoại có trong danh bạ hay không, 
- số trusted hay không. 

Nếu:

- có trong danh bạ → bỏ qua AI, 
- số lạ → tiếp tục xử lý. 
-----
**Bước 3 — Popup xác nhận**

Ứng dụng hiển thị notification nổi:

Ví dụ:

SMS từ số lạ detected.\
\
Bạn có muốn kiểm tra lừa đảo không?

-----
**Bước 4 — Local AI xử lý sơ bộ**

Local AI thực hiện:

- keyword detection, 
- URL detection, 
- blacklist lookup. 
-----
**Bước 5 — Online AI Processing**

Nếu có internet:

- gửi dữ liệu lên FastAPI, 
- AI server phân tích sâu hơn. 
-----
**Bước 6 — AI Risk Score**

AI tính:

- keyword score, 
- blacklist score, 
- URL score, 
- ML confidence score. 
-----
**Bước 7 — Overlay Warning**

Hiển thị:

- Risk Score, 
- loại scam, 
- khuyến nghị. 

Ví dụ:

⚠ Scam Warning\
\
Risk Score: 94%\
\
Fake Bank Scam

-----
**Bước 8 — Đề xuất block số**

Nếu:

Risk Score >= 80

thì hiển thị:

Block số điện thoại này?

-----
**3. SEQUENCE DIAGRAM PHÂN TÍCH CUỘC GỌI**

**3.1. Mục tiêu**

Mô tả:

- realtime call detection, 
- speech-to-text, 
- AI transcript analysis, 
- overlay realtime warning. 
-----
**3.2. Sequence Diagram**

User\
` `|\
` `| Cuộc gọi đến\
` `v\
Android Telephony Manager\
` `|\
` `v\
Call Listener Service\
` `|\
` `| Kiểm tra danh bạ\
` `v\
Contacts Manager\
` `|\
` `| Số lạ?\
` `+-------------------------+\
` `| NO                      | YES\
` `|                         |\
` `| Bỏ qua                  |\
` `|                         v\
` `|                  Notification Manager\
` `|                         |\
` `|             Hiển thị popup xác nhận\
` `|                         |\
` `|      "Bạn có muốn phân tích cuộc gọi?"\
` `|                         |\
` `|<-------- User ----------|\
` `|                         |\
` `| Người dùng đồng ý       |\
` `|                         v\
` `|                  Speech-to-Text Engine\
` `|                         |\
` `|                  Convert Audio\
` `|                         |\
` `|                  Generate Transcript\
` `|                         |\
` `|                  Local AI Engine\
` `|                         |\
` `|              Keyword Detection\
` `|                         |\
` `|                 Có internet?\
` `|                         |\
` `|          +--------------+-------------+\
` `|          | YES                        | NO\
` `|          |                            |\
` `|          v                            v\
` `|     FastAPI Backend           Local AI Result\
` `|          |\
` `|          v\
` `|    AI Detection Engine\
` `|          |\
` `|    NLP Transcript Analysis\
` `|          |\
` `|    Risk Score Engine\
` `|          |\
` `|    Generate Scam Result\
` `|          |\
` `|          v\
` `|    Overlay Warning Service\
` `|          |\
` `| Hiển thị realtime warning\
` `|          |\
` `| "Fake Police Scam"\
` `| "Không cung cấp OTP"\
` `|          |\
` `| Risk Score > 85?\
` `|          |\
` `| YES\
` `|          |\
` `| Hiển thị:\
` `| "Block số điện thoại?"\
` `|          |\
` `|<-------- User --------->\
` `|          |\
` `| Đồng ý\
` `|          |\
` `|          v\
` `| Android Block Manager\
` `|          |\
` `| Block Number\
` `|          |\
` `| SQLite Database

-----
**3.3. Giải thích chi tiết flow cuộc gọi**

-----
**Bước 1 — Cuộc gọi đến**

Android TelephonyManager phát hiện:

CALL\_STATE\_RINGING

-----
**Bước 2 — Kiểm tra danh bạ**

Nếu:

- số quen → bỏ qua, 
- số lạ → hiển thị popup. 
-----
**Bước 3 — Người dùng xác nhận**

Popup:

Bạn có muốn AI kiểm tra cuộc gọi này không?

-----
**Bước 4 — Speech-to-Text**

Hệ thống:

- ghi audio, 
- convert thành transcript, 
- gửi transcript cho AI. 
-----
**Bước 5 — AI NLP Processing**

AI phân tích:

- nội dung hội thoại, 
- hành vi scam, 
- mẫu giả danh. 
-----
**Bước 6 — Realtime Overlay**

Overlay hiển thị:

- Risk Score, 
- loại scam, 
- cảnh báo. 

Ví dụ:

⚠ Fake Police Scam\
\
Risk Score: 96%\
\
Không chuyển tiền\
Không cung cấp OTP

-----
**Bước 7 — Block Suggestion**

Nếu:

Risk Score >= 85

thì đề xuất block.

-----
**4. SEQUENCE DIAGRAM REPORT SCAM**

User\
` `|\
` `| Report Scam\
` `v\
Android App\
` `|\
` `v\
FastAPI Backend\
` `|\
` `v\
Validation Layer\
` `|\
` `v\
MySQL Database\
` `|\
` `v\
Update Blacklist\
` `|\
` `v\
Return Success\
` `|\
` `v\
Android App\
` `|\
` `Hiển thị:\
` `"Report submitted successfully"

-----
**5. SEQUENCE DIAGRAM BLACKLIST SYNC**

Android App\
` `|\
` `| Request blacklist\
` `v\
FastAPI Backend\
` `|\
` `v\
MySQL Database\
` `|\
` `v\
Return blacklist\
` `|\
` `v\
Android App\
` `|\
` `Update SQLite Database

-----
**6. SEQUENCE DIAGRAM OFFLINE MODE**

SMS / Call\
`     `|\
Local AI Engine\
`     `|\
Keyword Detection\
`     `|\
Blacklist Lookup\
`     `|\
Risk Score\
`     `|\
Overlay Warning

-----
**7. ASYNCHRONOUS PROCESSING FLOW**

Hệ thống sử dụng async processing cho:

- AI API, 
- Speech-to-Text, 
- Overlay, 
- Database logging. 
-----
**7.1. Async SMS Analysis**

Coroutine Start\
`      `|\
Background Thread\
`      `|\
API Call\
`      `|\
Receive Result\
`      `|\
Main Thread UI Update

-----
**7.2. Async Call Analysis**

Audio Stream\
`      `|\
Background Processing\
`      `|\
Speech-to-Text\
`      `|\
AI Analysis\
`      `|\
Realtime Overlay Update

-----
**8. THREADING ARCHITECTURE**

|**Thành phần**|**Thread**|
| :-: | :-: |
|SMS Listener|Background|
|Call Listener|Background|
|AI API|IO Thread|
|Speech-to-Text|Worker Thread|
|Overlay UI|Main Thread|
|SQLite|IO Thread|

-----
**9. ERROR HANDLING FLOW**

**9.1. Server Error**

API Error\
`   `|\
Fallback Local AI\
`   `|\
Show Offline Warning

-----
**9.2. No Internet**

No Internet\
`    `|\
Local Detection\
`    `|\
Limited Analysis

-----
**10. SECURITY FLOW**

Android App\
`     `|\
JWT Authentication\
`     `|\
HTTPS Encryption\
`     `|\
FastAPI Backend

-----
**11. KẾT LUẬN**

Sequence Diagram hiện tại mô tả đầy đủ:

- SMS processing flow, 
- Call realtime flow, 
- AI detection flow, 
- async processing, 
- overlay warning, 
- block suggestion, 
- offline fallback, 
- blacklist synchronization. 

Tài liệu này giúp:

- AI code Android chính xác hơn, 
- giảm lỗi realtime, 
- giảm lỗi async/threading, 
- hỗ trợ FastAPI backend, 
- hỗ trợ AI pipeline development. 


**BỔ SUNG — SEQUENCE DIAGRAMS: ERROR HANDLING & OFFLINE MODE**

Tài liệu gốc chỉ có happy path (luồng thành công). Phần này bổ sung các sequence diagram quan trọng cho trường hợp lỗi và offline, giúp developer xử lý đúng mọi tình huống.

**Sequence 1: SMS Detection — Offline Mode (Không có mạng)**

Khi thiết bị mất kết nối internet, hệ thống phải tự động fallback sang Local AI mà không cần user can thiệp.

**Actors: User, Android App, NetworkMonitor, LocalAIEngine, Room DB, NotificationService**

|**Bước**|**Actor Gửi**|**Actor Nhận**|**Hành động / Message**|
| - | - | - | - |
|1|System|Android App|onSmsReceived(phoneNumber, message)|
|2|SmsAnalysisService|NetworkMonitor|isNetworkAvailable()|
|3|NetworkMonitor|SmsAnalysisService|return false (No Network)|
|4|SmsAnalysisService|SmsAnalysisService|[alt: offline] Log: "Switching to offline mode"|
|5|SmsAnalysisService|LocalAIEngine|analyzeOffline(message, phoneNumber)|
|6|LocalAIEngine|LocalAIEngine|Load Gemma-2B model (if not loaded)|
|7|LocalAIEngine|LocalAIEngine|Run keyword matching + rule engine|
|8|LocalAIEngine|SmsAnalysisService|return AnalysisResult(riskScore, category, isScam)|
|9|SmsAnalysisService|Room DB|saveToSmsHistory(result, processingMode=OFFLINE)|
|10|SmsAnalysisService|Room DB|saveToAiLogs(result, mode=OFFLINE)|
|11|SmsAnalysisService|NotificationService|showWarningNotification(result) [if riskScore >= threshold]|
|12|NotificationService|User|Hiển thị notification cảnh báo có badge "Offline"|
|13|NetworkMonitor|SmsAnalysisService|[khi mạng phục hồi] onNetworkAvailable()|
|14|SmsAnalysisService|FastAPI Server|Sync offline results to cloud (background)|

**Lưu ý quan trọng:**

•  Gemma-2B model phải được load sẵn trong memory sau lần khởi động đầu tiên, không load lại mỗi lần phân tích.

•  Kết quả offline được đánh dấu processing\_mode=OFFLINE trong Room DB để sync lên cloud sau.

•  Accuracy offline thấp hơn online khoảng 15-20% — cần thông báo cho user biết.

**Sequence 2: SMS Detection — API Timeout / Server Error**

Khi server FastAPI không phản hồi trong thời gian timeout (5 giây), app tự động fallback mà không để user chờ.

|**Bước**|**Actor Gửi**|**Actor Nhận**|**Hành động / Message**|
| - | - | - | - |
|1|SmsAnalysisService|NetworkMonitor|isNetworkAvailable() → true|
|2|SmsAnalysisService|FastAPI Server|POST /api/analyze/sms (timeout=5s)|
|3|FastAPI Server|SmsAnalysisService|[5 giây trôi qua — không có response]|
|4|SmsAnalysisService|SmsAnalysisService|catch TimeoutException — log warning|
|5|SmsAnalysisService|LocalAIEngine|[fallback] analyzeOffline(message)|
|6|LocalAIEngine|SmsAnalysisService|return AnalysisResult (offline result)|
|7|SmsAnalysisService|Room DB|saveResult(processingMode=OFFLINE, note=API\_TIMEOUT)|
|8|SmsAnalysisService|NotificationService|showWarning(result) với badge "Phân tích nhanh"|
|9|SmsAnalysisService|RetryQueue|addToRetryQueue(originalRequest) — retry sau 5 phút|
|10|RetryQueue|FastAPI Server|[5 phút sau] retry request để đối chiếu kết quả|

**Sequence 3: Call Analysis — STT Engine Fail**

Khi Speech-to-Text không thể chuyển đổi giọng nói (tiếng ồn, accent không nhận được, RECORD\_AUDIO bị thu hồi...), hệ thống chỉ dùng Caller ID detection.

|**Bước**|**Actor Gửi**|**Actor Nhận**|**Hành động / Message**|
| - | - | - | - |
|1|System|CallDetectionService|onCallReceived(phoneNumber)|
|2|CallDetectionService|FastAPI Server|GET /api/scam-phone/{phoneNumber} (Caller ID check)|
|3|FastAPI Server|CallDetectionService|return { exists: true, risk\_level: HIGH }|
|4|CallDetectionService|OverlayService|showCallWarning(callerIdResult) — hiển thị overlay ngay|
|5|CallDetectionService|STTEngine|startRecording() — bắt đầu ghi âm|
|6|STTEngine|STTEngine|[alt: STT fail] Exception: NoSpeechDetected hoặc AudioQualityTooLow|
|7|STTEngine|CallDetectionService|onSTTError(errorCode)|
|8|CallDetectionService|CallDetectionService|Log STT failure, tiếp tục với Caller ID result|
|9|CallDetectionService|OverlayService|updateOverlay("Không thể phân tích giọng nói — Dựa trên số điện thoại")|
|10|CallDetectionService|Room DB|saveCallHistory(callerIdOnly=true, sttSuccess=false)|
|11|System|CallDetectionService|onCallEnded()|
|12|CallDetectionService|OverlayService|dismissOverlay()|

**Sequence 4: Authentication — Token Refresh Flow**

Khi access\_token hết hạn (sau 24h), app tự động làm mới token mà không yêu cầu user đăng nhập lại.

|**Bước**|**Actor Gửi**|**Actor Nhận**|**Hành động / Message**|
| - | - | - | - |
|1|SmsAnalysisService|ApiService|POST /api/analyze/sms (với expired token)|
|2|FastAPI Server|ApiService|return 401 Unauthorized|
|3|ApiService|AuthInterceptor|onTokenExpired() — intercept 401|
|4|AuthInterceptor|TokenStorage|getRefreshToken()|
|5|TokenStorage|AuthInterceptor|return refreshToken|
|6|AuthInterceptor|FastAPI Server|POST /api/auth/refresh { refresh\_token }|
|7a|FastAPI Server|AuthInterceptor|[success] return { access\_token, expires\_in }|
|8a|AuthInterceptor|TokenStorage|saveNewAccessToken(newToken)|
|9a|AuthInterceptor|ApiService|retry original request với token mới|
|7b|FastAPI Server|AuthInterceptor|[alt: refresh hết hạn] return 401|
|8b|AuthInterceptor|AuthManager|onRefreshExpired() — force logout|
|9b|AuthManager|User|Redirect về LoginScreen, xóa token|

**Sequence 5: Blacklist Sync — Background Update**

Blacklist được đồng bộ định kỳ trong background bằng WorkManager, đảm bảo app luôn có dữ liệu mới nhất ngay cả khi user không mở app.

|**Bước**|**Actor Gửi**|**Actor Nhận**|**Hành động / Message**|
| - | - | - | - |
|1|WorkManager|BlacklistSyncWorker|doWork() — triggered mỗi 6 giờ|
|2|BlacklistSyncWorker|SharedPreferences|getLastSyncTimestamp()|
|3|BlacklistSyncWorker|FastAPI Server|GET /api/blacklist?since={lastSync}|
|4|FastAPI Server|BlacklistSyncWorker|return { data: [...newEntries], total: 150 }|
|5|BlacklistSyncWorker|Room DB|upsertBlacklist(newEntries) — INSERT OR REPLACE|
|6|BlacklistSyncWorker|SharedPreferences|saveLastSyncTimestamp(now)|
|7|BlacklistSyncWorker|WorkManager|return Result.success()|
|8 [if fail]|BlacklistSyncWorker|WorkManager|return Result.retry() — retry sau 15 phút|

**Sequence 6: First Launch — Permission Request Flow**

Luồng xin permission lần đầu khi user cài app, theo thứ tự tối ưu tỷ lệ chấp nhận.

|**Bước**|**Actor Gửi**|**Actor Nhận**|**Hành động / Message**|
| - | - | - | - |
|1|System|OnboardingActivity|onCreate() — app khởi động lần đầu|
|2|OnboardingActivity|PermissionManager|checkAllPermissions()|
|3|PermissionManager|OnboardingActivity|return PermissionStatus(missing=[POST\_NOTIFICATIONS, RECEIVE\_SMS, ...])|
|4|OnboardingActivity|User|Hiển thị màn hình giải thích tổng quan các quyền cần thiết|
|5|User|OnboardingActivity|Tap "Bắt đầu thiết lập"|
|6|OnboardingActivity|PermissionManager|requestNotificationPermission() [Android 13+]|
|7|System|User|Hiển thị system dialog: "Cho phép thông báo?"|
|7a|User|PermissionManager|[GRANTED] tiếp tục bước 8|
|7b|User|PermissionManager|[DENIED] hiển thị rationale dialog, cho phép bỏ qua|
|8|OnboardingActivity|PermissionManager|requestPermissions([RECEIVE\_SMS, READ\_CONTACTS, READ\_PHONE\_STATE])|
|9|System|User|Hiển thị system dialogs từng cái một|
|10|OnboardingActivity|PermissionManager|requestRecordAudioPermission() — khi user bật Call Analysis|
|11|OnboardingActivity|PermissionManager|requestOverlayPermission() — mở Settings|
|12|OnboardingActivity|HomeActivity|startActivity(HomeActivity) — hoàn thành setup|

**Lưu ý về UX:**

•  Không xin tất cả permission cùng một lúc — user sẽ từ chối hết.

•  Mỗi permission phải có màn hình giải thích riêng TRƯỚC khi system dialog xuất hiện.

•  App vẫn hoạt động được nếu thiếu một số permission — chỉ giảm tính năng.

