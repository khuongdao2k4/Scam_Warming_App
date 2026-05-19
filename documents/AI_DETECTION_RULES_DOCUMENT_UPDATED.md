**AI DETECTION RULES DOCUMENT**

Hệ thống Ứng dụng Cảnh báo Lừa đảo qua Tin nhắn và Cuộc gọi

*Phiên bản 1.1 — Cập nhật & Chuẩn hóa*
# **1. GIỚI THIỆU**
## **1.1. Mục đích tài liệu**
Tài liệu AI Detection Rules mô tả toàn bộ logic phát hiện lừa đảo, bao gồm Risk Score Engine, Decision Engine, Keyword Detection, URL Detection, Blacklist Detection, NLP Analysis, Rule-based AI và Machine Learning AI.

Mục tiêu:

- Giúp AI Engine hoạt động chính xác và nhất quán
- Hỗ trợ AI code generation hiểu đúng thuật toán
- Hỗ trợ FastAPI backend và Android realtime detection

## **1.2. Phạm vi áp dụng**
Hệ thống AI áp dụng cho các loại đầu vào: SMS Detection, Call Transcript Detection, URL Detection, Spam Detection, Phishing Detection và Fake Identity Detection.

## **1.3. Các loại lừa đảo được hỗ trợ phát hiện**

|**Loại lừa đảo (Scam Type)**|**Mô tả**|**Mức độ nguy hiểm**|
| :- | :- | :- |
|OTP Scam|Lừa lấy mã OTP từ người dùng|🔴 Nguy hiểm cao|
|Fake Bank|Giả mạo ngân hàng để chiếm đoạt tài khoản|🔴 Nguy hiểm cao|
|Fake Police|Giả danh công an, viện kiểm sát để tống tiền|🔴 Nguy hiểm cao|
|Phishing|Link giả mạo dẫn đến trang web lừa đảo|🟠 Nguy hiểm|
|Investment Scam|Lừa đảo đầu tư lợi nhuận cao bất thường|🟠 Nguy hiểm|
|Lottery Scam|Thông báo trúng thưởng giả để lừa chuyển tiền|🟠 Nguy hiểm|
|Delivery Scam|Giả mạo dịch vụ giao hàng để lấy thông tin|🟡 Cần chú ý|
|Tech Support Scam|Giả hỗ trợ kỹ thuật để cài malware|🟡 Cần chú ý|
|Loan Scam|Lừa đảo vay tiền với điều kiện hấp dẫn|🟡 Cần chú ý|
|Job Scam|Lừa tuyển dụng để chiếm đoạt thông tin/tiền|🟡 Cần chú ý|

# **2. AI DETECTION ARCHITECTURE**
Toàn bộ pipeline phân tích AI hoạt động theo chuỗi xử lý tuần tự, mỗi bước bổ sung điểm rủi ro vào Risk Score tổng:

|**1.**  Input — SMS hoặc Transcript cuộc gọi|
| :- |
|▼|
|**2.**  Preprocessing — Chuẩn hóa văn bản, trích xuất URL, tokenization|
|▼|
|**3.**  Keyword Detection — Dò từ khóa nguy hiểm, tính Keyword Score|
|▼|
|**4.**  Blacklist Detection — Kiểm tra số điện thoại và URL trong blacklist|
|▼|
|**5.**  URL Detection — Phân tích URL rút gọn, domain giả, IP address|
|▼|
|**6.**  Pattern Matching — So khớp mẫu scam đã biết|
|▼|
|**7.**  NLP Analysis — Phân tích ngữ nghĩa, hành vi thao túng tâm lý|
|▼|
|**8.**  Risk Score Engine — Tổng hợp tất cả điểm thành Risk Score (0–100)|
|▼|
|**9.**  Decision Engine — Quyết định mức cảnh báo và hành động|
|▼|
|**10.**  Warning Output — Hiển thị overlay / notification cho người dùng|

# **3. INPUT PREPROCESSING**
## **3.1. Chuẩn hóa văn bản**
Tiền xử lý dữ liệu trước khi AI phân tích nhằm chuẩn hóa đầu vào, tránh bỏ sót từ khóa do khác viết hoa/thường.

|**Bước xử lý**|**Mô tả**|**Ví dụ**|
| :- | :- | :- |
|Lowercase|Chuyển toàn bộ sang chữ thường|"CHUYỂN KHOẢN" → "chuyển khoản"|
|Unicode Normalize|Chuẩn hóa ký tự Unicode tiếng Việt|Thống nhất mã hóa dấu|
|Remove Special Chars|Xóa ký tự đặc biệt không cần thiết|"NGAY!!!" → "ngay"|
|Remove Extra Spaces|Xóa khoảng trắng thừa|"chuyển  khoản" → "chuyển khoản"|
|URL Extraction|Tách URL ra khỏi văn bản để xử lý riêng|"bấm bit.ly/abc" → URL list|
|Tokenization|Tách câu thành các token từ|"chuyển khoản ngay" → ["chuyển", "khoản", "ngay"]|

## **3.2. URL Extraction**
Regex phát hiện HTTP/HTTPS link, shortened URL và domain đáng ngờ:

|<p>**Regex URL Detection**</p><p>https?:\/\/[^\s]+  |  (bit\.ly|tinyurl\.com|goo\.gl|shorturl\.at)\/\S+</p>|
| :- |

# **4. KEYWORD DETECTION ENGINE**
## **4.1. Keyword Risk Scoring**
Mỗi từ khóa nguy hiểm được gán điểm rủi ro. Điểm được cộng vào tổng Risk Score khi từ khóa xuất hiện trong văn bản:

|**Từ khóa**|**Risk Score**|**Nhóm**|
| :- | :- | :- |
|công an|+35|Fake Authority|
|viện kiểm sát|+35|Fake Authority|
|OTP|+30|Account Takeover|
|đầu tư lợi nhuận|+30|Investment Scam|
|chuyển khoản|+25|Financial Transfer|
|tài khoản bị khóa|+25|Account Threat|
|xác minh tài khoản|+20|Account Takeover|
|đăng nhập ngay|+20|Account Takeover|
|cập nhật thông tin|+20|Phishing|
|hoàn tiền|+20|Financial Fraud|
|ngân hàng|+15|Fake Bank|
|mã xác nhận|+15|Account Takeover|
|nhận thưởng|+15|Lottery Scam|
|vay tiền|+15|Loan Scam|
|giao hàng|+10|Delivery (low risk)|

## **4.2. Keyword Combination Rules — Điểm cộng thêm**
Khi nhiều từ khóa kết hợp, Risk Score tăng thêm Bonus Score để phản ánh ngữ cảnh nguy hiểm hơn:

|**Tổ hợp từ khóa**|**Bonus Score**|**Lý do**|
| :- | :- | :- |
|công an + chuyển tiền|+50|Fake Police yêu cầu chuyển tiền — Critical|
|OTP + chuyển khoản|+40|Kết hợp chiếm OTP + rút tiền|
|đầu tư + lợi nhuận cao|+40|Investment Scam pattern điển hình|
|ngân hàng + tài khoản bị khóa|+35|Fake Bank threatening pattern|
|nhận thưởng + link|+30|Lottery Scam + phishing URL|

## **4.3. Dangerous Sentence Patterns**

|**Pattern câu nguy hiểm**|**Risk Level**|**Hành động**|
| :- | :- | :- |
|"Chuyển khoản ngay"|Critical|Score >= 80|
|"Cung cấp OTP"|High|Score += 30|
|"Tài khoản sẽ bị khóa"|High|Score += 25|
|"Bấm vào link"|Medium|Score += 20|
|"Xác minh thông tin"|Medium|Score += 20|

# **5. URL DETECTION ENGINE**
## **5.1. URL Risk Rules**

|**Điều kiện**|**Điểm thêm**|**Ví dụ**|
| :- | :- | :- |
|URL rút gọn (shortened)|+25|bit.ly/abc123|
|Domain giả ngân hàng|+50|vietcombank-security.net|
|Domain trong URL blacklist|+70|Đã có trong DB phishing|
|URL chứa IP Address|+30|http://192.168.1.1/bank|
|HTTP (không HTTPS)|+15|http://bank-secure.com|
|Có URL (bất kỳ)|+20|Cộng thêm khi phát hiện URL|

## **5.2. Shortened URL Domains — Danh sách phát hiện**

|**Domain rút gọn**|**Nguy cơ**|
| :- | :- |
|bit.ly|Phổ biến nhất, thường dùng che giấu URL|
|tinyurl.com|Dịch vụ rút gọn cổ điển|
|goo.gl|Đã ngừng hoạt động nhưng vẫn xuất hiện trong scam cũ|
|shorturl.at|Dịch vụ rút gọn khác|
|t.co|Twitter shortener — ít nguy hiểm hơn|

## **5.3. Fake Domain Detection**
Kẻ lừa đảo thường tạo domain giả bằng cách thêm tiền tố/hậu tố vào tên thương hiệu thật:

|**Domain giả (Fake)**|**Domain thật (Whitelist)**|**Pattern phát hiện**|
| :- | :- | :- |
|vietcombank-security.com|vietcombank.com.vn|brand-keyword.tld|
|mbbank-support.net|mbbank.com.vn|brand-support.tld|
|vietinbank-verify.info|vietinbank.com.vn|brand-verify.tld|
|techcombank-login.xyz|techcombank.com.vn|brand-login.tld|
|bidv-security.org|bidv.com.vn|brand-security.tld|

# **6. BLACKLIST DETECTION ENGINE**
## **6.1. Phone Blacklist Rules**

|**Điều kiện**|**Điểm thêm**|**Ghi chú**|
| :- | :- | :- |
|Số trong blacklist (xác nhận)|+60|Đã được xác nhận là scam|
|Số có Report > 100 lần|+60|Cộng đồng báo cáo nhiều|
|Số có Report > 50 lần|+40|Mức báo cáo cao|
|Số có Report > 10 lần|+20|Mức báo cáo trung bình|

## **6.2. URL Blacklist Rules**

|**Điều kiện**|**Điểm thêm**|
| :- | :- |
|URL nằm trong blacklist phishing|+70|
|Domain trong danh sách phishing (VirusTotal API)|+80|

# **7. PATTERN MATCHING ENGINE**
Pattern Matching phát hiện mẫu scam lặp lại, nội dung spam hàng loạt và transcript tương tự các case scam đã biết.

|**Pattern**|**Risk Level**|**Điểm**|**Ví dụ**|
| :- | :- | :- | :- |
|Fake Bank Call|Critical|+60|"Ngân hàng VCB — tài khoản của bạn bị đóng băng"|
|Fake Police Threat|Critical|+60|"Công an TP.HCM thông báo bạn liên quan vụ án..."|
|OTP Verification Request|High|+40|"Cung cấp mã OTP để xác nhận giao dịch"|
|Investment High Return|High|+35|"Đầu tư 10 triệu, nhận lại 50 triệu sau 30 ngày"|
|Fake Delivery|Medium|+20|"Đơn hàng bị giữ, cần thanh toán phí giải phóng"|

# **8. NLP ANALYSIS ENGINE**
## **8.1. Psychological Manipulation Detection**
AI phân tích ngữ nghĩa và ngữ cảnh để phát hiện các kỹ thuật thao túng tâm lý điển hình của kẻ lừa đảo:

|**Hành vi thao túng**|**Risk Score**|**Ví dụ điển hình**|
| :- | :- | :- |
|Đe dọa (Threatening)|+30|"Nếu không làm ngay sẽ bị bắt"|
|Yêu cầu bí mật (Secrecy)|+25|"Không được nói với ai"|
|Gây hoảng loạn (Panic)|+25|"Tài khoản bị hack ngay bây giờ!"|
|Tạo áp lực thời gian (Urgency)|+20|"Còn 5 phút để xác nhận"|

## **8.2. Emotion Detection**

|**Trạng thái cảm xúc phát hiện**|**Risk Level**|**Điểm**|
| :- | :- | :- |
|Panic (Hoảng loạn)|Critical|+30|
|Fear (Sợ hãi)|High|+25|
|Urgency (Khẩn cấp)|High|+20|

# **9. CALL TRANSCRIPT DETECTION**
## **9.1. Transcript Risk Rules**

|**Điều kiện trong transcript cuộc gọi**|**Điểm thêm**|**Risk Level**|
| :- | :- | :- |
|Giả danh công an/viện kiểm sát|+60|Critical|
|Yêu cầu chuyển tiền ngay|+50|Critical|
|Giả danh ngân hàng|+50|High|
|Yêu cầu cung cấp OTP|+40|High|
|Yêu cầu cài ứng dụng lạ|+35|High|

## **9.2. Realtime Chunk Processing**
AI phân tích transcript theo từng đoạn (~5 giây) trong khi cuộc gọi vẫn đang diễn ra. Risk Score được cập nhật liên tục:

|**Thời điểm**|**Chunk**|**Score tích lũy**|**Trạng thái**|
| :- | :- | :- | :- |
|0:05|"Xin chào, tôi từ ngân hàng VCB..."|Score: 15|Chưa cảnh báo|
|0:10|"...tài khoản của bạn bị đóng băng..."|Score: 40|Suspicious|
|0:15|"...cần xác minh OTP ngay lập tức..."|Score: 75|Nguy hiểm — Overlay xuất hiện|
|0:20|"...chuyển tiền để giải phóng tài khoản"|Score: 100|Scam — Cảnh báo đỏ Critical|

# **10. MACHINE LEARNING MODEL**
## **10.1. Vai trò của ML Model**
ML Model học từ dữ liệu thực tế để phát hiện các pattern scam mới chưa có trong rule-based engine:

- Học scam pattern từ lịch sử báo cáo cộng đồng
- Phân tích keyword frequency theo ngữ cảnh
- NLP behavior analysis trên tiếng Việt
- Cải thiện độ chính xác theo thời gian (online learning)

## **10.2. Training Data Sources**

|**Nguồn dữ liệu**|**Loại**|**Mô tả**|
| :- | :- | :- |
|Community reports|Labeled|Số điện thoại + kịch bản được người dùng báo cáo|
|SMS report history|Labeled|SMS đã phân tích + label scam/safe|
|Transcript scam|Labeled|Transcript cuộc gọi đã xác nhận lừa đảo|
|Phishing URL database|Labeled|URL trong VirusTotal, APWG Phishing|
|Synthetic data|Augmented|Biến thể câu scam được tạo tự động|

## **10.3. AI Confidence Score Mapping**

|**Confidence Score**|**Ý nghĩa**|**Màu hiển thị**|
| :- | :- | :- |
|81 – 100|Critical — Rất chắc chắn là scam|🔴 Đỏ|
|61 – 80|High — Khả năng cao là scam|🟠 Cam|
|31 – 60|Medium — Nghi ngờ, cần xem thêm|🟡 Vàng|
|0 – 30|Low — Có thể an toàn|🟢 Xanh|

# **11. RISK SCORE ENGINE**
## **11.1. Công thức tổng hợp**

|<p>**Risk Score Formula**</p><p>Risk Score = Keyword Score + URL Score + Blacklist Score + Pattern Score + NLP Score + ML Confidence  Clamp: Nếu tổng > 100 → đặt về 100 (tối đa)</p>|
| :- |

## **11.2. Risk Level Mapping**

|**Score Range**|**Risk Level**|**Màu**|**Hành động**|
| :- | :- | :- | :- |
|**0 – 30**|**SAFE**|Xanh lá 🟢|Không cảnh báo|
|**31 – 60**|**SUSPICIOUS**|Vàng 🟡|Thông báo nhẹ|
|**61 – 80**|**DANGEROUS**|Cam 🟠|Overlay cảnh báo|
|**81 – 100**|**SCAM**|Đỏ 🔴|Overlay đỏ + gợi ý block|

# **12. DECISION ENGINE**
## **12.1. Decision Rules**

|**Risk Level**|**Score**|**Action**|**UI hiển thị**|
| :- | :- | :- | :- |
|SAFE|0–30|Không cảnh báo|Không hiển thị gì|
|SUSPICIOUS|31–60|Thông báo cảnh báo nhẹ|Notification màu vàng|
|DANGEROUS|61–80|Hiển thị Overlay|Overlay màu cam, có nút xem chi tiết|
|SCAM|81–100|Overlay đỏ + gợi ý block|Overlay đỏ nổi bật, nút Chặn số ngay|

## **12.2. Auto Block Suggestion**

|<p>**Điều kiện gợi ý chặn số**</p><p>Khi Risk Score >= 85: Hiển thị dialog xác nhận: "Số điện thoại này có dấu hiệu lừa đảo cao. Bạn có muốn chặn số này không?" Người dùng phải xác nhận — hệ thống KHÔNG tự động chặn.</p>|
| :- |

# **13. OFFLINE AI RULES**
## **13.1. Khi nào dùng Offline Mode**
- Thiết bị mất kết nối internet
- API server không phản hồi sau 5 giây timeout
- Người dùng bật chế độ Offline trong Settings

## **13.2. Khả năng phát hiện trong Offline Mode**

|**Tính năng phát hiện**|**Offline hỗ trợ**|**Ghi chú**|
| :- | :- | :- |
|Keyword Detection|✅ Đầy đủ|Keyword list lưu local|
|URL Detection|✅ Đầy đủ|Regex rules lưu local|
|Blacklist Detection|✅ Đầy đủ|Blacklist cache local (sync định kỳ)|
|Pattern Matching|✅ Đầy đủ|Rule patterns lưu local|
|NLP Analysis|⚠️ Giới hạn|Gemma-2B trên thiết bị — độ chính xác thấp hơn|
|ML Cloud Model|❌ Không có|Cần kết nối server|

# **14. FALSE POSITIVE REDUCTION**
## **14.1. Trusted Contact Filter**
Khi số điện thoại nằm trong danh bạ hoặc trusted number list, hệ thống giảm mạnh Risk Score để tránh cảnh báo sai với người thân/bạn bè:

- Số trong danh bạ Android: bỏ qua hoàn toàn (Score = 0)
- Số trong Trusted Numbers list: bỏ qua hoàn toàn (Score = 0)

## **14.2. Trusted Brand Whitelist**
Danh sách domain/số điện thoại của các tổ chức uy tín được whitelist — không bao giờ bị cảnh báo:

|**Loại whitelist**|**Ví dụ**|
| :- | :- |
|Ngân hàng thật (domain)|vietcombank.com.vn, mbbank.com.vn, bidv.com.vn|
|OTP Service|Brandname SMS từ VCB-SMSBanking, BIDV...|
|Verified Delivery|GHTK, GHN, Viettel Post brandname|
|Government domains|.gov.vn domains|

# **15. REALTIME WARNING RULES**
## **15.1. SMS Warning Thresholds**

|**Score**|**Action**|**UI Element**|
| :- | :- | :- |
|Score >= 30|Push notification|Notification bar — màu vàng|
|Score >= 60|Popup warning|Dialog cảnh báo màu cam — cần tap để đóng|
|Score >= 80|Red overlay|Full overlay đỏ — nổi bật, khó bỏ qua|

## **15.2. Call Warning Thresholds**

|**Score**|**Action**|**UI Element**|
| :- | :- | :- |
|Score >= 40|Yellow warning|Overlay nhỏ góc màn hình màu vàng|
|Score >= 70|Orange warning|Overlay lớn hơn màu cam, thông tin chi tiết|
|Score >= 85|Red critical warning|Full overlay đỏ, nút Cúp máy ngay|

# **16. LOGGING & ANALYTICS**
## **16.1. AI Logs — Dữ liệu ghi lại**
- Số điện thoại nguồn
- Loại nguồn: SMS hoặc CALL
- Risk Score cuối cùng
- Từ khóa phát hiện (keyword\_matched)
- Chế độ xử lý: ONLINE hoặc OFFLINE
- Thời gian xử lý (ms)
- Phiên bản model AI đã dùng

## **16.2. Analytics Metrics**

|**Metric**|**Mô tả**|**Dùng để**|
| :- | :- | :- |
|Top keywords|Từ khóa xuất hiện nhiều nhất|Cập nhật keyword list|
|Scam trends|Xu hướng lừa đảo theo thời gian|Cảnh báo sớm pattern mới|
|Category distribution|Phân bố loại scam|Tối ưu detection rules|
|False positive rate|Tỷ lệ cảnh báo sai|Tinh chỉnh threshold|
|Processing time|Thời gian phản hồi trung bình|Tối ưu hiệu năng|

# **17. BẢO MẬT & HIỆU NĂNG**
## **17.1. Quy tắc bảo mật dữ liệu**

|**Quy tắc**|**Mô tả**|
| :- | :- |
|Không upload audio gốc|Chỉ gửi transcript text đã xử lý, không gửi file âm thanh|
|Không upload nội dung SMS gốc|Chỉ gửi metadata và risk score kết quả|
|Không lưu danh bạ trên server|Danh bạ chỉ được xử lý local trên thiết bị|
|Data minimization|Chỉ gửi đúng dữ liệu cần thiết cho phân tích|

## **17.2. Performance Requirements**

|**Thành phần**|**Target**|**Max cho phép**|
| :- | :- | :- |
|SMS Detection (online)|< 2 giây|< 5 giây|
|SMS Detection (offline)|< 500ms|< 1 giây|
|Call chunk processing|< 3 giây/chunk|< 5 giây|
|Overlay hiển thị|< 1 giây|< 2 giây|
|Keyword scan|< 100ms|< 300ms|

# **18. KẾT LUẬN**
AI Detection Engine hỗ trợ đầy đủ các chức năng phát hiện lừa đảo, được thiết kế theo nguyên tắc:

|**Nguyên tắc thiết kế**|**Mô tả**|
| :- | :- |
|Realtime|Phát hiện và cảnh báo trong vòng 2–3 giây|
|Privacy-first|Không lưu trữ nội dung nhạy cảm trên server|
|Hybrid AI|Kết hợp online (accuracy cao) và offline (always available)|
|Scalable|Dễ mở rộng keyword list, rule set và ML model|
|Vietnamese-optimized|Tối ưu hóa đặc biệt cho tiếng Việt và kịch bản lừa đảo tại Việt Nam|

*— Hết tài liệu —*
