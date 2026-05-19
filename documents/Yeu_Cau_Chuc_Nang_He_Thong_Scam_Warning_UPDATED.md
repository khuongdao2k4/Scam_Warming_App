**TÀI LIỆU YÊU CẦU CHỨC NĂNG HỆ THỐNG**\


**Ứng dụng cảnh báo lừa đảo qua tin nhắn và cuộc gọi**

\
Tài liệu này mô tả toàn bộ yêu cầu chức năng của hệ thống ứng dụng cảnh báo lừa đảo qua tin nhắn SMS và cuộc gọi điện thoại trên nền tảng Android. \
Hệ thống có nhiệm vụ phát hiện các dấu hiệu lừa đảo theo thời gian thực, cảnh báo người dùng và hỗ trợ bảo vệ an toàn thông tin cá nhân.
# **6.1. Chức năng quản lý và phân tích SMS**
## **FR-SMS-01 — Phát hiện tin nhắn SMS đến**
Mục đích:\
Phát hiện khi có SMS mới gửi đến thiết bị.\
\
Luồng xử lý:\
\- BroadcastReceiver nhận sự kiện SMS\_RECEIVED.\
\- Trích xuất số điện thoại gửi, nội dung và thời gian gửi.\
\- Chuyển dữ liệu sang module kiểm tra danh bạ.\
\
Kết quả:\
\- SMS được đưa vào hàng đợi xử lý.
## **FR-SMS-02 — Kiểm tra số điện thoại trong danh bạ**
Mục đích:\
Không phân tích tin nhắn từ số điện thoại đã lưu trong danh bạ.\
\
Luồng xử lý:\
\- Đối chiếu số điện thoại với ContactsContract.\
\- Nếu số đã tồn tại trong danh bạ: dừng toàn bộ quá trình phân tích.\
\- Nếu là số lạ: tiếp tục hiển thị yêu cầu kiểm tra.\
\
Kết quả:\
\- Chỉ các SMS từ số lạ mới được phân tích.
## **FR-SMS-03 — Hiển thị yêu cầu kiểm tra SMS**
Mục đích:\
Cho phép người dùng quyết định có muốn kiểm tra SMS hay không.\
\
Giao diện:\
\- Overlay hoặc Notification nổi.\
\- Nội dung ví dụ: “Bạn có muốn kiểm tra tin nhắn này có dấu hiệu lừa đảo không?”\
\
Tùy chọn:\
\- Kiểm tra ngay\
\- Bỏ qua\
\
Kết quả:\
\- Chỉ khi người dùng đồng ý thì hệ thống mới bắt đầu phân tích.
## **FR-SMS-04 — Phân tích nội dung SMS**
Mục đích:\
Phát hiện dấu hiệu lừa đảo trong nội dung SMS.\
\
Luồng xử lý:\
\- Phân tích từ khóa nguy hiểm.\
\- Phân tích mẫu hành vi đáng ngờ.\
\- Kiểm tra đường link giả mạo.\
\- Đối chiếu blacklist số điện thoại.\
\- Tính Risk Score.\
\
Kết quả:\
\- Mức độ nguy hiểm.\
\- Lý do cảnh báo.\
\- Điểm Risk Score.
## **FR-SMS-05 — Hiển thị kết quả phân tích SMS**
Mục đích:\
Thông báo kết quả phân tích cho người dùng.\
\
Mức độ cảnh báo:\
\- 0–30: An toàn\
\- 31–60: Nghi ngờ\
\- 61–100: Nguy hiểm\
\
Giao diện:\
\- Popup\
\- Notification\
\- Overlay màu xanh, vàng hoặc đỏ.
## **FR-SMS-06 — Đề xuất chặn số điện thoại SMS lừa đảo**
Mục đích:\
Hỗ trợ người dùng chặn số điện thoại nguy hiểm.\
\
Luồng xử lý:\
\- Khi Risk Score vượt ngưỡng nguy hiểm, hệ thống hiển thị:\
“Số điện thoại này có dấu hiệu lừa đảo. Bạn có muốn chặn không?”\
\- Nếu người dùng chọn “Có”: hệ thống tự động gọi BlockedNumbers API để chặn số.\
\- Nếu chọn “Không”: không thực hiện chặn.\
\
Lưu ý:\
\- Hệ thống không tự động chặn nếu chưa có xác nhận từ người dùng.
# **6.2. Chức năng quản lý và phân tích cuộc gọi**
## **FR-CALL-01 — Phát hiện cuộc gọi đến**
Mục đích:\
Phát hiện cuộc gọi mới thông qua PhoneStateListener.\
\
Trạng thái theo dõi:\
\- RINGING\
\- OFFHOOK\
\- IDLE
## **FR-CALL-02 — Kiểm tra số điện thoại trong danh bạ**
Mục đích:\
Không phân tích cuộc gọi từ số quen.\
\
Luồng xử lý:\
\- Kiểm tra số điện thoại trong danh bạ.\
\- Nếu là số quen: dừng giám sát.\
\- Nếu là số lạ: hiển thị yêu cầu kiểm tra.
## **FR-CALL-03 — Hiển thị yêu cầu kiểm tra cuộc gọi**
Mục đích:\
Xin xác nhận từ người dùng trước khi phân tích.\
\
Giao diện:\
\- Overlay nổi.\
\- Nội dung ví dụ:\
“Bạn có muốn kiểm tra cuộc gọi này có dấu hiệu lừa đảo không?”\
\
Tùy chọn:\
\- Đồng ý kiểm tra\
\- Bỏ qua
## **FR-CALL-04 — Phát hiện giả mạo danh tính**
Mục đích:\
Phát hiện Caller ID Spoofing.\
\
Luồng xử lý:\
\- So sánh tên hiển thị với danh bạ.\
\- Nếu tên giống người quen nhưng số điện thoại thực tế khác:\
hiển thị cảnh báo giả mạo danh tính.\
\
Kết quả:\
\- Overlay cảnh báo màu đỏ.
## **FR-CALL-05 — Ghi âm cuộc gọi**
Mục đích:\
Thu âm cuộc gọi phục vụ phân tích.\
\
Công nghệ:\
\- MediaRecorder\
\
Luồng xử lý:\
\- Khi trạng thái OFFHOOK:\
bắt đầu ghi âm.\
\- Audio được chia thành từng đoạn ngắn khoảng 5 giây.
## **FR-CALL-06 — Chuyển giọng nói thành văn bản**
Mục đích:\
Chuyển audio thành text.\
\
Công nghệ:\
\- Google Speech-to-Text\
\- Vosk Offline\
\
Kết quả:\
\- Văn bản hội thoại phục vụ phân tích.
## **FR-CALL-07 — Phân tích nội dung cuộc gọi**
Mục đích:\
Phát hiện kịch bản lừa đảo trong hội thoại.\
\
Nội dung phân tích:\
\- Giả danh công an\
\- OTP\
\- Chuyển khoản\
\- Đầu tư tài chính\
\- Việc làm online\
\- Trúng thưởng\
\
Kết quả:\
\- Risk Score\
\- Mức độ nguy hiểm.
## **FR-CALL-08 — Cảnh báo thời gian thực**
Mục đích:\
Cảnh báo ngay trong khi cuộc gọi đang diễn ra.\
\
Điều kiện:\
\- Risk Score vượt ngưỡng nguy hiểm.\
\
Giao diện:\
\- Overlay màu đỏ.\
\- Gợi ý hành động:\
\+ Không cung cấp OTP\
\+ Không chuyển khoản\
\+ Hãy cúp máy ngay
## **FR-CALL-09 — Đề xuất chặn số điện thoại sau cuộc gọi**
Mục đích:\
Cho phép người dùng chặn số điện thoại sau khi kết thúc cuộc gọi.\
\
Luồng xử lý:\
\- Khi cuộc gọi kết thúc:\
nếu xác định là lừa đảo, hệ thống hỏi:\
“Bạn có muốn chặn số điện thoại này không?”\
\- Nếu người dùng đồng ý:\
hệ thống tự động chặn bằng Android BlockedNumbers API.
# **6.3. Chức năng lưu trữ dữ liệu**
## **FR-DATA-01 — Lưu lịch sử phân tích trên thiết bị**
Mục đích:\
Lưu lịch sử để người dùng xem lại.\
\
Nơi lưu:\
\- SQLite Local Database\
\
Dữ liệu lưu:\
\- Số điện thoại\
\- Nội dung\
\- Risk Score\
\- Kết quả phân tích\
\- Thời gian\
\
Lưu ý:\
\- Dữ liệu chi tiết không gửi lên cloud.
## **FR-DATA-02 — Chỉ lưu metadata lừa đảo lên cloud**
Mục đích:\
Xây dựng cơ sở dữ liệu cộng đồng.\
\
Điều kiện:\
\- Chỉ gửi dữ liệu khi xác định là lừa đảo.\
\
Dữ liệu được gửi:\
\- Số điện thoại\
\- Loại lừa đảo\
\- Risk Score\
\- Thời gian phát hiện\
\
Không gửi:\
\- Nội dung SMS đầy đủ\
\- Audio cuộc gọi\
\- Dữ liệu cá nhân nhạy cảm
## **FR-DATA-03 — Đồng bộ blacklist từ cloud**
Mục đích:\
Cập nhật danh sách số điện thoại lừa đảo mới.\
\
Luồng xử lý:\
\- Ứng dụng kiểm tra cập nhật định kỳ.\
\- Tải blacklist mới.\
\- Đồng bộ về local database.
# **6.4. Chức năng AI và Backend**
## **FR-AI-01 — Phân tích online qua FastAPI**
Mục đích:\
Tăng độ chính xác khi có Internet.\
\
Công nghệ:\
\- FastAPI\
\- Gemini API\
\- Groq API
## **FR-AI-02 — Phân tích offline bằng AI cục bộ**
Mục đích:\
Cho phép hệ thống hoạt động khi mất mạng.\
\
Công nghệ:\
\- Gemma-2B 4-bit
## **FR-AI-03 — Tự động chuyển đổi online/offline**
Mục đích:\
Đảm bảo hệ thống hoạt động liên tục.\
\
Luồng xử lý:\
\- Có Internet → sử dụng Online AI.\
\- Mất Internet → chuyển sang Offline AI.
# **6.5. Chức năng bảo mật và quyền riêng tư**
## **FR-SEC-01 — Mã hóa dữ liệu truyền tải**
Mục đích:\
Bảo vệ dữ liệu người dùng.\
\
Công nghệ:\
\- HTTPS\
\- TLS
## **FR-SEC-02 — Không lưu audio sau phân tích**
Mục đích:\
Bảo vệ quyền riêng tư.\
\
Luồng xử lý:\
\- File audio tạm thời sẽ bị xóa sau khi hoàn tất phân tích.
## **FR-SEC-03 — Xin quyền truy cập rõ ràng**
Các quyền yêu cầu:\
\- READ\_SMS\
\- RECEIVE\_SMS\
\- RECORD\_AUDIO\
\- READ\_CONTACTS\
\- READ\_PHONE\_STATE\
\- SYSTEM\_ALERT\_WINDOW\
\
Mục đích:\
Đảm bảo ứng dụng hoạt động đúng chức năng và minh bạch với người dùng.


# **TÀI LIỆU TIẾP THEO CẦN THỰC HIỆN**

Sau khi hoàn thiện tài liệu yêu cầu chức năng, bước tiếp theo của dự án là xây dựng:\
1\. Database Design Document\
2\. API Specification\
3\. Use Case Diagram\
4\. UI/UX Screen Flow\
5\. Sequence Diagram\
6\. AI Detection Rule Document\
\
Các tài liệu này sẽ giúp AI có thể sinh mã nguồn Android App, Backend FastAPI, Database và AI Pipeline chính xác hơn.

**7. YÊU CẦU PHI CHỨC NĂNG (NON-FUNCTIONAL REQUIREMENTS)**

Phần này mô tả các yêu cầu về hiệu năng, bảo mật, tương thích và độ tin cậy mà hệ thống phải đáp ứng. Các yêu cầu này cần được kiểm tra song song với yêu cầu chức năng.

**7.1. Yêu cầu hiệu năng (Performance)**

|**ID**|**Thành phần**|**Metric**|**Target**|**Max cho phép**|
| :- | :- | :- | :- | :- |
|NFR-P-01|SMS Detection (online)|Response time|< 2 giây|< 5 giây|
|NFR-P-02|SMS Detection (offline)|Response time|< 500ms|< 1 giây|
|NFR-P-03|Call STT per chunk|Latency|< 3 giây|< 5 giây|
|NFR-P-04|Overlay hiển thị|Time to display|< 1 giây|< 2 giây|
|NFR-P-05|App cold start|Startup time|< 3 giây|< 5 giây|
|NFR-P-06|Blacklist sync|Background time|< 30 giây|< 60 giây|
|NFR-P-07|API throughput|Requests/phút|100 req/min/user|200 req/min|
|NFR-P-08|Battery usage|Mức tiêu thụ|< 2% mỗi giờ|< 5% mỗi giờ|
|NFR-P-09|RAM usage|Footprint|< 150MB|< 250MB|

**7.2. Yêu cầu tương thích (Compatibility)**

|**ID**|**Thành phần**|**Min Version**|**Target**|**Ghi chú**|
| :- | :- | :- | :- | :- |
|NFR-C-01|Android OS|Android 8.0 (API 26)|Android 14 (API 34)|SYSTEM\_ALERT\_WINDOW từ API 23+|
|NFR-C-02|RAM thiết bị|2GB|4GB|Gemma-2B cần min 2GB free|
|NFR-C-03|Storage|500MB free|2GB free|Model AI ~1.5GB|
|NFR-C-04|Mạng|3G|4G/WiFi|Offline mode cho 2G/no-network|
|NFR-C-05|Ngôn ngữ|Tiếng Việt|Tiếng Việt|Ưu tiên tối ưu tiếng Việt|

**7.3. Yêu cầu bảo mật (Security)**

|**ID**|**Yêu cầu**|**Mô tả chi tiết**|
| :- | :- | :- |
|NFR-S-01|HTTPS bắt buộc|Toàn bộ API call phải dùng HTTPS/TLS 1.2+. Không cho phép HTTP trong production.|
|NFR-S-02|JWT expiry|Access token expire sau 24h. Refresh token expire sau 30 ngày.|
|NFR-S-03|Privacy SMS|Nội dung SMS KHÔNG được upload server. Chỉ gửi metadata và risk score.|
|NFR-S-04|Privacy Audio|Audio cuộc gọi KHÔNG lưu server. Chỉ gửi transcript text đã xử lý.|
|NFR-S-05|Room Encryption|Room Database Android phải dùng SQLCipher mã hóa AES-256.|
|NFR-S-06|SharedPreferences|Token JWT phải lưu trong EncryptedSharedPreferences.|
|NFR-S-07|User consent|App phải xin phép người dùng trước khi thu thập bất kỳ dữ liệu nào (PDPA compliant).|
|NFR-S-08|Certificate Pinning|Production build phải dùng SSL Pinning để chống MITM.|
|NFR-S-09|ProGuard|Release build phải bật ProGuard/R8 để obfuscate code.|

**7.4. Yêu cầu độ tin cậy (Reliability)**

|**ID**|**Yêu cầu**|**Target**|
| :- | :- | :- |
|NFR-R-01|API Uptime|99\.5% uptime mỗi tháng|
|NFR-R-02|Offline mode|Core features (keyword detection, blacklist) hoạt động 100% khi mất internet|
|NFR-R-03|Crash rate|< 0.1% sessions bị crash|
|NFR-R-04|Data loss|Không mất lịch sử đã lưu local khi app crash hoặc thiết bị khởi động lại|
|NFR-R-05|Graceful degradation|Khi API timeout: fallback Local AI, hiển thị cảnh báo offline mode|

**7.5. Yêu cầu khả năng dùng (Usability)**

|**ID**|**Yêu cầu**|**Mô tả**|
| :- | :- | :- |
|NFR-U-01|Cảnh báo rõ ràng|Màu sắc overlay phải phân biệt rõ: Xanh (an toàn), Vàng (nghi ngờ), Cam (nguy hiểm), Đỏ (lừa đảo)|
|NFR-U-02|Người dùng cao tuổi|Font chữ tối thiểu 16sp, nút bấm tối thiểu 48dp, không dùng gesture phức tạp|
|NFR-U-03|Giải thích rõ|Mỗi cảnh báo phải hiển thị lý do cụ thể (ví dụ: Phát hiện từ khóa OTP + link rút gọn)|
|NFR-U-04|Không tự động|Hệ thống KHÔNG tự động chặn số. Luôn hỏi xác nhận người dùng trước khi block.|

**8. TEST CASE — KIỂM THỬ CHỨC NĂNG**

Phần này liệt kê các test case cốt lõi để kiểm tra tính đúng đắn của hệ thống AI Detection, API và flow chính.

**8.1. Test SMS Detection**

|**TC ID**|**Input SMS (tóm tắt)**|**Expected Risk Score**|**Expected Category**|**Ghi chú**|
| :- | :- | :- | :- | :- |
|TC-SMS-01|Chuyển khoản ngay 50 triệu cho tôi|>= 80|SCAM Critical|Keyword: chuyển khoản|
|TC-SMS-02|OTP của bạn là 123456 — VCB|0–25|Safe|Whitelist OTP service|
|TC-SMS-03|Trúng thưởng 100tr, bấm bit.ly/xxx|>= 85|Lottery Scam|Keyword + shortened URL|
|TC-SMS-04|Công an Hà Nội: tài khoản liên quan vụ án|>= 90|Fake Police|Critical combination|
|TC-SMS-05|GHTK: đơn #12345 đang giao, ship COD 50k|0–25|Safe|Delivery normal|
|TC-SMS-06|MBBank: tài khoản đăng nhập bất thường|31–60|Suspicious|Cần theo dõi thêm|
|TC-SMS-07|Đầu tư lợi nhuận 50%/tháng, uy tín|>= 70|Investment Scam|High investment keyword|
|TC-SMS-08|SMS từ số đã có trong danh bạ|0 (bỏ qua)|Skipped|Trusted contact filter|

**8.2. Test URL Detection**

|**TC ID**|**URL Input**|**Score thêm**|**Lý do phát hiện**|
| :- | :- | :- | :- |
|TC-URL-01|bit.ly/abc123|+25|Shortened URL domain|
|TC-URL-02|vietcombank-security.net/login|+70|Fake bank domain pattern|
|TC-URL-03|http://192.168.1.1/bank|+45|IP address URL + HTTP (không HTTPS)|
|TC-URL-04|https://vietcombank.com.vn|0|Whitelist — domain thật|
|TC-URL-05|mbbank-support.info/verify|+65|Fake subdomain pattern|
|TC-URL-06|tinyurl.com/xyz|+25|Shortened URL|

**8.3. Test Risk Score Engine**

|**TC ID**|**Keyword Score**|**URL Score**|**Blacklist Score**|**NLP Score**|**Total Expected**|
| :- | :- | :- | :- | :- | :- |
|TC-SCORE-01|70 (OTP+CK)|25 (bit.ly)|0|10|>= 85 → Block suggest|
|TC-SCORE-02|85 (CAN+CT)|0|60 (blacklist)|15|100 (clamped)|
|TC-SCORE-03|10 (giao hàng)|0|0|0|10 → Safe|
|TC-SCORE-04|60 (bank+lock)|70 (fake domain)|0|20|100 (clamped)|
|TC-SCORE-05|0|0|0|0|0 → No warning|

**8.4. Test API Endpoints**

|**TC ID**|**Endpoint**|**Điều kiện**|**Expected HTTP**|**Expected Response**|
| :- | :- | :- | :- | :- |
|TC-API-01|POST /api/analyze/sms|SMS hợp lệ, JWT hợp lệ|200|risk\_score, is\_scam, category|
|TC-API-02|POST /api/analyze/sms|message rỗng/null|400|Validation error|
|TC-API-03|POST /api/analyze/sms|Không có JWT header|401|Unauthorized|
|TC-API-04|GET /api/scam-phone/0901234567|Số trong blacklist|200|exists: true, risk\_level|
|TC-API-05|GET /api/scam-phone/0123456789|Số không tồn tại|200|exists: false|
|TC-API-06|POST /api/auth/register|Phone mới, device\_id hợp lệ|201|access\_token, refresh\_token|
|TC-API-07|POST /api/auth/register|Phone đã tồn tại|400|Duplicate error|
|TC-API-08|POST /api/auth/refresh|Refresh token còn hạn|200|access\_token mới|
|TC-API-09|POST /api/auth/refresh|Refresh token hết hạn|401|Unauthorized|
|TC-API-10|GET /api/blacklist|JWT hợp lệ|200|Array blacklist|
|TC-API-11|POST /api/report|Dữ liệu hợp lệ|201|success: true|
|TC-API-12|DELETE /api/trusted-numbers/0901234567|Số tồn tại|200|success: true|

**8.5. Test Permission Flow Android**

|**TC ID**|**Kịch bản**|**Expected Behavior**|
| :- | :- | :- |
|TC-PERM-01|User từ chối RECEIVE\_SMS|App chỉ detect số điện thoại, không phân tích nội dung SMS|
|TC-PERM-02|User từ chối RECORD\_AUDIO|Tắt tính năng phân tích cuộc gọi, hiển thị warning giải thích|
|TC-PERM-03|User từ chối READ\_CONTACTS|Không filter trusted contacts, phân tích tất cả số lạ|
|TC-PERM-04|SYSTEM\_ALERT\_WINDOW bị từ chối|Dùng notification thay thế overlay, không crash app|
|TC-PERM-05|Android 13+ không grant POST\_NOTIFICATIONS|Hiển thị hướng dẫn vào Settings để bật tay, không crash|

