THUYẾT MINH ĐỀ TÀI  |  ỨNG DỤNG CẢNH BÁO LỪA ĐẢO QUA TIN NHẮN VÀ CUỘC GỌI

**TRƯỜNG ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á**

`  `**KHOA CÔNG NGHỆ THÔNG TIN**

LỚP: DCCNTT 13.10.11

**CUỘC THI VIETFUTURE ADWARDS 2026**

**THUYẾT MINH ĐỀ TÀI**

**ỨNG DỤNG CẢNH BÁO LỪA ĐẢO**

**QUA TIN NHẮN VÀ CUỘC GỌI**

**Sinh viên thực hiện:**

**Đào Minh Khương  –  Phí Đình Huynh**

Lớp: DCCNTT 13.10.11

Năm học: 2026 – 2027


# **I.  THÔNG TIN CHUNG VỀ ĐỀ TÀI**

|**Tên đề tài**|Ứng dụng cảnh báo lừa đảo qua tin nhắn và cuộc gọi|
| :- | :- |
|**Sinh viên thực hiện**|Đào Minh Khương – Phí Đình Huynh|
|**Lớp**|DCCNTT 13.10.11|
|**Loại sản phẩm**|Ứng dụng Mobile (Android)|
|**Công nghệ chính**|Android (Java/Kotlin) · FastAPI (Python) · MySQL · Gemma-2B (Offline AI)|
|**Năm thực hiện**|2024 – 2025|

# **II.  BỐI CẢNH VÀ LÝ DO CHỌN ĐỀ TÀI**

## **2.1. Bối cảnh thực tiễn**
Trong bối cảnh chuyển đổi số đang diễn ra mạnh mẽ tại Việt Nam, các thiết bị di động đã trở thành công cụ giao tiếp và giao dịch không thể thiếu trong cuộc sống hàng ngày. Tuy nhiên, sự phát triển nhanh chóng này cũng kéo theo sự gia tăng đáng lo ngại của các hình thức lừa đảo trực tuyến thông qua tin nhắn SMS và cuộc gọi điện thoại.

Các thủ đoạn lừa đảo ngày càng tinh vi, đa dạng và khó nhận biết:

- Giả mạo ngân hàng, cơ quan nhà nước để chiếm đoạt thông tin tài khoản
- Lừa đảo trúng thưởng, cơ hội đầu tư với lợi nhuận hấp dẫn
- Cuộc gọi mạo danh công an, viện kiểm sát để tống tiền
- Tin nhắn chứa đường dẫn độc hại (phishing links) dẫn đến trang web giả mạo
- Scam tình cảm, lừa đảo tuyển dụng việc làm online

## **2.2. Lý do lựa chọn đề tài**
Người dùng thông thường, đặc biệt là người lớn tuổi và người ít am hiểu về công nghệ, thường trở thành nạn nhân do thiếu công cụ hỗ trợ nhận diện và cảnh báo kịp thời. Từ thực tế đó, nhóm nhận thấy sự cần thiết phải xây dựng một ứng dụng thông minh, có khả năng:

- Tự động phân tích và phát hiện dấu hiệu lừa đảo trong tin nhắn và cuộc gọi
- Cảnh báo người dùng theo thời gian thực trước khi họ trở thành nạn nhân
- Hỗ trợ phòng ngừa rủi ro tài chính và bảo vệ thông tin cá nhân
- Đặc biệt thân thiện với nhóm người dùng lớn tuổi, ít kinh nghiệm công nghệ

# **III.  MỤC TIÊU VÀ PHẠM VI ĐỀ TÀI**

## **3.1. Mục tiêu tổng quát**
Xây dựng một ứng dụng di động trên nền tảng Android, có khả năng phát hiện và cảnh báo người dùng về các nội dung có dấu hiệu lừa đảo trong tin nhắn SMS và cuộc gọi điện thoại theo thời gian thực.

## **3.2. Mục tiêu cụ thể**
- Phát triển hệ thống phân tích nội dung tin nhắn SMS với độ chính xác cao
- Xây dựng chức năng ghi âm và chuyển đổi giọng nói thành văn bản (Speech-to-Text) để phân tích nội dung cuộc gọi
- Thiết kế thuật toán tính điểm rủi ro (Risk Score) dựa trên nhiều tiêu chí
- Cung cấp cảnh báo tức thời khi phát hiện nguy cơ lừa đảo
- Xây dựng cơ sở dữ liệu số điện thoại và từ khóa lừa đảo được cập nhật liên tục
- Tạo báo cáo thống kê và lịch sử cảnh báo cho người dùng

## **3.3. Phạm vi thực hiện**
- Nền tảng: Ứng dụng Android (phiên bản 8.0 trở lên)
- Đối tượng phân tích: Tin nhắn SMS và cuộc gọi điện thoại đến
- Ngôn ngữ hỗ trợ: Tiếng Việt
- Phạm vi triển khai: Môi trường thử nghiệm, có thể mở rộng thực tế

# **IV.  KIẾN TRÚC VÀ THIẾT KẾ HỆ THỐNG**

## **4.1. Mô tả tổng quan hệ thống**
Ứng dụng được thiết kế theo kiến trúc Client – Server, trong đó ứng dụng Android đóng vai trò client thu thập dữ liệu từ thiết bị, gửi lên máy chủ backend để phân tích, sau đó nhận kết quả và hiển thị cảnh báo cho người dùng.

## **4.2. Các thành phần kiến trúc**

|**Thành phần**|**Công nghệ**|**Chức năng**|
| :-: | :-: | :-: |
|**Mobile App**|Android (Java/Kotlin)|Giao diện người dùng, thu thập SMS/cuộc gọi, hiển thị cảnh báo|
|**Backend API**|Python FastAPI|Nhận dữ liệu, phân tích nội dung, tính điểm rủi ro, trả kết quả|
|**Cơ sở dữ liệu**|MySQL|Lưu trữ từ khóa lừa đảo, số điện thoại đen, lịch sử cảnh báo|
|**Speech-to-Text**|Google Cloud STT (online) / Vosk (offline)|Chuyển đổi âm thanh cuộc gọi thành văn bản để phân tích|

## **4.3. Luồng hoạt động hệ thống**
Luồng xử lý chính của ứng dụng bao gồm 5 bước liên tiếp:

1. Thu thập: Ứng dụng nhận SMS hoặc phát hiện cuộc gọi đến
1. Tiền xử lý: Trích xuất nội dung văn bản / chuyển âm thanh sang text (STT)
1. Gửi lên Backend: Nội dung text + số điện thoại được gửi qua API
1. Phân tích & Tính điểm: Backend tính Risk Score theo công thức
1. Cảnh báo: Trả kết quả về app, hiển thị thông báo cho người dùng

# **V.  SO SÁNH CHI TIẾT VÀ ƯU THẾ CẠNH TRANH**
Phần này tập trung vào việc chứng minh tại sao ứng dụng của bạn lại cần thiết ngay cả  khi đã có các "ông lớn" trên thị trường.
## **5.1. Bảng so sánh tính năng chuyên sâu**

|**Tiêu chí**|**Các ứng dụng phổ biến (Truecaller, Whoscall)**|**Ứng dụng của bạn (Scam Warning)**|
| :-: | :-: | :-: |
|**Cơ chế cốt lõi**|**Phản ứng (Reactive):** Dựa vào việc người dùng báo cáo (Report) sau khi đã bị gọi.|**Chủ động (Proactive):** Phân tích nội dung hội thoại/tin nhắn ngay khi đang diễn ra để cảnh báo sớm.|
|**Phân tích nội dung**|**Không có:** Chỉ nhận diện số điện thoại trong danh sách đen.|**Phân tích sâu:** Sử dụng NLP (Xử lý ngôn ngữ tự nhiên) để nhận diện kịch bản lừa đảo (đe dọa, dụ dỗ).|
|**Xử lý cuộc gọi**|Chỉ hiển thị tên hoặc nhãn "Lừa đảo" (nếu có trong DB).|**Speech-to-Text (STT):** Chuyển lời nói thành văn bản để máy chủ phân tích ngữ nghĩa theo thời gian thực.|
|**Độ tối ưu bản địa**|Dữ liệu quốc tế, chưa cập nhật sâu các kịch bản lừa đảo đặc thù tại VN (giả danh Công an, Viện kiểm sát...).|**Tối ưu hóa Tiếng Việt:** Xây dựng bộ từ điển và mẫu hành vi lừa đảo dựa trên thực tế tội phạm mạng tại Việt Nam.|
|**Trải nghiệm người dùng**|Nhiều quảng cáo, tính năng gây nhiễu, khó dùng cho người già.|Chế độ bảo vệ người thân: Giao diện tối giản, cảnh báo bằng màu sắc mạnh mẽ (Đỏ/Vàng) và gửi tin nhắn cho người thân.|
## **5.2. Ưu thế đột phá (Unique Selling Points – USP)**
- **Hàng rào bảo vệ kép:** Không chỉ chặn số, mà còn hiểu nội dung. Điều này giúp ngăn chặn cả những số điện thoại "rác" mới chưa có trong danh sách đen.
- **Cảnh báo thời gian thực (Real-time Overlay):** Thông tin cảnh báo được đẩy lên ngay trên màn hình cuộc gọi, giúp người dùng dừng cuộc hội thoại trước khi thực hiện các thao tác chuyển tiền.
# **VI.  CHỨC NĂNG CHI TIẾT CỦA SẢN PHẨM**
## **6.1. Phát hiện lừa đảo qua SMS**
- Tự động đọc và phân tích nội dung tin nhắn SMS đến
- Đối chiếu với cơ sở dữ liệu từ khóa lừa đảo (scam\_keywords)
- Kiểm tra số điện thoại gửi tin nhắn với danh sách đen (scam\_phones)
- Phát hiện các đường dẫn (URL) đáng ngờ hoặc giả mạo
- Hiển thị cảnh báo ngay lập tức với mức độ nguy hiểm rõ ràng

## **6.2. Phân tích và cảnh báo cuộc gọi**
- Phát hiện cuộc gọi đến từ số điện thoại nằm trong danh sách đen
- Ghi âm cuộc gọi qua micro và chuyển đổi thành văn bản
- Phân tích nội dung hội thoại để phát hiện kịch bản lừa đảo
- Hiển thị cảnh báo overlay trong khi cuộc gọi đang diễn ra
## **6.3. Thuật toán tính điểm rủi ro (Risk Score)**
Hệ thống sử dụng công thức tổng hợp đa tiêu chí:

**Risk Score  =  Keyword Score  +  Pattern Score  +  Phone Score  +  Link Score**

- Keyword Score: Đánh giá dựa trên mật độ và mức độ nguy hiểm của từ khóa lừa đảo
- Pattern Score: Nhận diện mẫu hành vi lừa đảo (tạo áp lực thời gian, đe dọa, cam kết lợi nhuận cao)
- Phone Score: Kiểm tra số điện thoại đối chiếu với cơ sở dữ liệu đã được gắn cờ
- Link Score: Phát hiện URL đáng ngờ, tên miền giả mạo, liên kết rút gọn
## **6.4. Quản lý báo cáo và lịch sử**
- Lưu trữ toàn bộ lịch sử cảnh báo đã được phát hiện
- Báo cáo thống kê số lượng tin nhắn/cuộc gọi lừa đảo theo thời gian
- Cho phép người dùng xem chi tiết và đánh giá lại kết quả phân tích
- Hỗ trợ xuất báo cáo và chia sẻ thông tin cảnh báo
# **VII.  CƠ SỞ DỮ LIỆU**
## **7.1. Các bảng dữ liệu chính**

|**Tên bảng**|**Mô tả**|
| :-: | :-: |
|**scam\_phones**|Danh sách đen các số điện thoại đã được xác nhận lừa đảo, bao gồm mức độ nguy hiểm và nguồn báo cáo|
|**scam\_keywords**|Từ điển các từ khóa, cụm từ đặc trưng của lừa đảo, được phân loại theo nhóm và trọng số điểm|
|**history**|Lịch sử toàn bộ các tin nhắn và cuộc gọi đã được phân tích, kết quả Risk Score và hành động của người dùng|
# **VIII.  CÔNG NGHỆ SỬ DỤNG**
## **8.1. Phía Client – Ứng dụng Android**
- Ngôn ngữ lập trình: Java / Kotlin
- SMS Receiver: BroadcastReceiver để nhận và đọc tin nhắn SMS
- Call Detection: PhoneStateListener để phát hiện cuộc gọi đến
- Audio Recording: MediaRecorder để ghi âm cuộc gọi qua micro
- Notification: Android Notification Manager cho cảnh báo tức thời
## **8.2. Phía Server – Backend (Hybrid Online/Offline)**
- Framework: Python FastAPI – hiệu suất cao, hỗ trợ async. Triển khai miễn phí trên Render Free Tier
- Cơ sở dữ liệu: MySQL (Supabase Free / PlanetScale Free) – lưu trữ từ khóa, số điện thoại đen và lịch sử cảnh báo
- Speech-to-Text: Google Cloud STT (online) hoặc Vosk (offline, miễn phí)
- AI Engine: Google Gemini API Free Tier / Groq API (online) + Gemma-2B 4-bit trên thiết bị (offline)
## **8.3. Bảo mật và quyền riêng tư**
- Dữ liệu người dùng được mã hóa trong quá trình truyền tải (HTTPS)
- Không lưu trữ nội dung tin nhắn hoặc âm thanh lên server sau khi phân tích
- Xử lý quyền truy cập thiết bị theo chuẩn Android Permissions
## **8.4. Quy trình xử lý dữ liệu thông minh (Data Pipeline)**
1. **Lớp Thu thập (Ingestion Layer):**
- **SMS:** Sử dụng BroadcastReceiver để lắng nghe sự kiện SMS\_RECEIVED. Hệ thống sẽ trích xuất nội dung tin nhắn và các URL đính kèm.
- **Call:** Sử dụng PhoneStateListener để xác định trạng thái cuộc gọi. Khi bắt đầu hội thoại, ứng dụng kích hoạt MediaRecorder để thu âm qua micro (với sự cho phép của người dùng).
1. **Lớp Tiền xử lý (Preprocessing Layer):**
- Sử dụng **Google Cloud Speech-to-Text API** để chuyển âm thanh thành văn bản. Văn bản được chuẩn hóa (loại bỏ từ thừa, sửa lỗi chính tả) trước khi gửi về Server.
1. **Lớp Phân tích (Analysis Layer - Trái tim của hệ thống):**
- **Thuật toán Tính điểm Rủi ro (Risk Scoring Algorithm):**

<b>Total_Score = (S<sub>1</sub> x W<sub>1</sub>) + (S<sub>2</sub> x W<sub>2</sub>) + (S<sub>3</sub> x W<sub>3</sub>) + (S<sub>4</sub> x W<sub>4</sub>)</b>

- <b>S<sub>1</sub></b>: Điểm danh sách đen (Số điện thoại đã bị báo cáo).
- <b>S<sub>2</sub></b>: Điểm từ khóa (Chứa các từ: "chuyển khoản", "lệnh bắt", "quà tặng", "mã OTP"...).
- <b>S<sub>3</sub></b>: Điểm mẫu hành vi (Nhận diện sự thúc ép về thời gian hoặc đe dọa).
- <b>S<sub>4</sub></b>: Điểm liên kết (Kiểm tra URL qua VirusTotal API hoặc danh sách Phishing).
- *Trọng số W được điều chỉnh để giảm tỷ lệ cảnh báo sai (False Positive).*
## **8.5. Giải pháp phía backend (FastAPI và Python)**
- **FastAPI:** Lựa chọn vì khả năng xử lý bất đồng bộ (Asynchronous) cực tốt, giúp phản hồi kết quả phân tích về điện thoại trong dưới 1 giây.
- **MySQL:** Lưu trữ dữ liệu có cấu trúc về các mẫu lừa đảo và lịch sử của người dùng một cách an toàn.
# **IX.  GIẢI PHÁP KỸ THUẬT BỔ SUNG**
## **9.1. Giải pháp Backend Hybrid (Online + Offline)**
Hệ thống sử dụng kiến trúc Hybrid Backend, kết hợp sức mạnh của điện toán đám mây và khả năng xử lý tại chỗ (Edge AI) nhằm đảm bảo ứng dụng hoạt động ổn định trong mọi điều kiện mạng, với chi phí triển khai tối thiểu (hoàn toàn miễn phí).

**Chế độ Online (Ưu tiên):** Khi có kết nối Internet, ứng dụng gửi dữ liệu văn bản đến FastAPI Backend được triển khai miễn phí trên Render hoặc Railway. Hệ thống gọi API của các LLM miễn phí (Google Gemini API Free Tier, Groq API) kết hợp cơ sở dữ liệu MySQL (PlanetScale Free hoặc Supabase) để trả về kết quả phân tích chính xác trong dưới 1 giây.

**Chế độ Offline (Dự phòng):** Khi mất kết nối mạng, hệ thống tự động chuyển sang mô hình AI nội bộ Gemma-2B (phiên bản quantized 4-bit) được tải sẵn vào thiết bị. Với thiết bị tối thiểu 6GB RAM, mô hình 4-bit (~1.5GB) chạy ổn định mà không gây nóng máy hay giật lag, đảm bảo phân tích cơ bản vẫn hoạt động 24/7 ngay cả ở khu vực sóng yếu.

**Chi phí triển khai:** Toàn bộ hạ tầng có thể vận hành miễn phí bằng cách tận dụng Render Free Tier (Backend), Supabase Free (Database), Google Gemini API Free (500 req/ngày), và mô hình Gemma-2B tải về từ Hugging Face.
## **9.2. Cơ chế Lọc Thông Minh – Chỉ Giám Sát Số Lạ**
Để tránh làm phiền người dùng và tối ưu hiệu suất, ứng dụng áp dụng cơ chế kiểm tra danh bạ trước khi kích hoạt bất kỳ phân tích nào.

**Luồng xử lý:** Khi có cuộc gọi đến, PhoneStateListener ngay lập tức đối chiếu số điện thoại với danh bạ hệ thống (ContactsContract). Nếu số đã có trong danh bạ → ứng dụng không kích hoạt, không hiển thị bất kỳ thông báo nào. Nếu là số lạ (chưa lưu danh bạ) → ứng dụng kích hoạt toàn bộ quy trình giám sát và phân tích.

**Lợi ích:** Giảm thiểu "cảnh báo sai" (False Positive), tiết kiệm pin và tài nguyên xử lý, đồng thời mang lại trải nghiệm tự nhiên cho người dùng.
## **9.3. Phát Hiện Giả Mạo Danh Tính (Caller ID Spoofing)**
Đây là kỹ thuật tinh vi khi kẻ lừa đảo dùng dịch vụ VoIP để hiển thị tên giả (ví dụ: "Công an TP.HCM", "Mẹ", "Ngân hàng Vietcombank") trong khi số điện thoại thực tế hoàn toàn khác. Ứng dụng xử lý vấn đề này bằng cơ chế so khớp kép:

**Bước 1 – Tách biệt hai nguồn thông tin:** Caller ID Name (tên hiển thị do nhà mạng/VoIP cung cấp) và Phone Number (số điện thoại thực nhận từ tín hiệu sóng di động).

**Bước 2 – So khớp kép:** Nếu tên hiển thị trùng với một tên trong danh bạ (ví dụ: "Mẹ") nhưng số điện thoại thực tế không khớp với số đã lưu dưới tên đó, ứng dụng lập tức gắn nhãn cảnh báo đỏ đậm "⚠ CẢNH BÁO: Giả mạo danh tính" và hiển thị Overlay ngay trên màn hình cuộc gọi.

**Nguyên tắc cốt lõi:** Ứng dụng luôn tin vào số điện thoại thực tế, không tin vào tên hiển thị – vì tên có thể bị làm giả, còn số điện thoại là dữ liệu kỹ thuật không thể che giấu hoàn toàn.
## **9.4. Luồng Hoạt Động Chi Tiết Của Một Cuộc Gọi**
Dưới đây là toàn bộ vòng đời xử lý từ khi cuộc gọi đến cho đến khi kết thúc và lưu kết quả:

**Giai đoạn 1 – Kích hoạt (Trigger):** PhoneStateListener phát hiện trạng thái RINGING → Kiểm tra danh bạ → Nếu là số lạ: kích hoạt giám sát.

**Giai đoạn 2 – Giám sát thời gian thực:** Khi người dùng nhấc máy (trạng thái OFFHOOK), MediaRecorder bắt đầu thu âm qua micro theo từng đoạn ngắn (~5 giây). Mỗi đoạn được chuyển thành văn bản qua Speech-to-Text (Google Cloud STT online hoặc Vosk offline).

**Giai đoạn 3 – Phân tích rủi ro:** Văn bản từng đoạn được gửi lên Backend (hoặc mô hình Offline) để tính Risk Score theo công thức đa tiêu chí. Kết quả được trả về liên tục trong khi cuộc gọi vẫn đang diễn ra.

**Giai đoạn 4 – Cảnh báo tức thì:** Nếu Risk Score vượt ngưỡng cảnh báo, một Overlay màu đỏ xuất hiện đè lên màn hình cuộc gọi, hiển thị mức độ nguy hiểm và gợi ý hành động (ví dụ: "Hãy cúp máy ngay!").

**Giai đoạn 5 – Kết thúc và xử lý hậu kỳ:** Khi cuộc gọi kết thúc (trạng thái IDLE): (1) Kết quả đánh giá được lưu vào lịch sử trên thiết bị (Local Storage). (2) Nếu xác định là lừa đảo: Metadata (số điện thoại, kịch bản, Risk Score) được đẩy lên Cloud Database để bảo vệ cộng đồng. (3) Ứng dụng đề xuất Chặn số ngay lập tức – người dùng xác nhận một chạm để kích hoạt BlockedNumbers API của Android.
## **9.5. Chiến Lược Lưu Trữ Dữ Liệu – Hybrid Storage**
Sau khi phân tích ưu nhược điểm của từng phương án, giải pháp tối ưu được chọn là Hybrid Storage: kết hợp lưu trữ cục bộ và đám mây theo từng loại dữ liệu.

**Lưu trên thiết bị (Local – SQLite):** Toàn bộ lịch sử cuộc gọi chi tiết, bản ghi âm tạm thời, và kết quả phân tích đầy đủ. Ưu điểm: tốc độ rất nhanh, không phụ thuộc mạng, quyền riêng tư cao nhất – dữ liệu nhạy cảm không rời khỏi thiết bị. Nhược điểm: bị mất nếu xóa ứng dụng, không thể đóng góp cảnh báo cho cộng đồng.

**Lưu trên Cloud (Supabase/PlanetScale Free):** Chỉ đẩy Metadata tối giản: số điện thoại lừa đảo, loại kịch bản (giả mạo công an, ngân hàng...), và Risk Score. Ưu điểm: cập nhật danh sách đen chung, bảo vệ người dùng khác trên toàn quốc. Nhược điểm: phụ thuộc Internet, cần mã hóa HTTPS.

**Kết luận lựa chọn:** Giải pháp Hybrid đảm bảo quyền riêng tư tối đa cho từng cá nhân, đồng thời xây dựng được trí tuệ cộng đồng (Community Intelligence) – điều mà các giải pháp thuần Local không thể làm được.

# **X.  KẾ HOẠCH THỰC HIỆN CHI TIẾT**
Chia lộ trình thành các cột mốc cụ thể để chứng minh tính chuyên nghiệp trong quản lý dự án.

|**Giai đoạn**|**Tuần**|**Công việc trọng tâm**|**Sản phẩm đầu ra (Deliverables)**|
| :-: | :-: | :-: | :-: |
|1\. Khởi động|1 - 3|Nghiên cứu 50+ kịch bản lừa đảo thực tế; thiết kế kiến trúc DB; hoàn thiện UI/UX (Figma).|Tài liệu phân tích yêu cầu & Bản thiết kế giao diện.|
|2\. Phát triển lõi (Core)|4 - 8|Xây dựng Backend API (FastAPI) + triển khai Gemma-2B 4-bit cho chế độ Offline; tích hợp Google STT và Vosk; thiết lập thuật toán tính điểm Risk Score.|Hệ thống Backend chạy ổn định trên môi trường thử nghiệm.|
|3\. Phát triển Mobile|9 - 14|Lập trình các Module: Đọc SMS, Ghi âm cuộc gọi, Hiển thị Overlay; Kết nối API với Backend.|Bản Alpha (APK) có thể chạy các chức năng cơ bản.|
|4\. Tích hợp & Kiểm thử|15 - 19|Kiểm thử với 100+ số điện thoại và tin nhắn mẫu; Tối ưu hóa hiệu năng (pin, RAM); Sửa lỗi (Bug fix).|Bản Beta hoàn thiện; Báo cáo kết quả kiểm thử độ chính xác (>80%).|
|5\. Đóng gói & Thuyết minh|20 - 24|Hoàn thiện tài liệu hướng dẫn; Chuẩn bị video demo; Viết báo cáo thuyết minh đề tài cuối cùng.|Đồ án hoàn chỉnh & Sản phẩm sẵn sàng trình chiếu.|

# **XI.  KẾT QUẢ DỰ KIẾN VÀ HẠN CHẾ**
## **11.1. Kết quả dự kiến đạt được**
- Ứng dụng Android hoàn chỉnh với giao diện thân thiện, dễ sử dụng
- Phát hiện chính xác tin nhắn SMS lừa đảo với tỉ lệ ít nhất 80%
- Cảnh báo người dùng trong vòng 3 giây sau khi nhận tin nhắn
- Hệ thống backend ổn định, xử lý đồng thời nhiều yêu cầu phân tích
- Cơ sở dữ liệu từ khóa và số điện thoại lừa đảo được cập nhật định kỳ
## **11.2. Hạn chế hiện tại**
- Hạn chế của Android: Không thể truy cập trực tiếp audio từ đường truyền cuộc gọi, chỉ có thể ghi âm qua micro – ảnh hưởng đến chất lượng âm thanh
- Phụ thuộc vào độ chính xác của Speech-to-Text: Giọng nói không rõ hoặc tiếng ồn có thể làm giảm độ chính xác phân tích
- Cần quyền truy cập SMS và micro: Người dùng cần cấp quyền để ứng dụng hoạt động đầy đủ
- Cập nhật cơ sở dữ liệu: Cần cơ chế tự động cập nhật danh sách lừa đảo mới

# **XII.  HƯỚNG PHÁT TRIỂN TRONG TƯƠNG LAI**

## **12.1. Nâng cấp công nghệ AI**
- Tích hợp mô hình Machine Learning (NLP) để phân tích ngữ nghĩa thay vì chỉ dựa vào từ khóa
- Ứng dụng mô hình Deep Learning để nhận diện giọng nói lừa đảo theo ngữ điệu
- Triển khai AI phát hiện Deepfake Voice – giọng nói giả mạo bằng AI

## **12.2. Mở rộng nền tảng**
- Phát triển phiên bản iOS để tiếp cận rộng hơn người dùng
- Xây dựng cộng đồng chia sẻ và báo cáo số điện thoại lừa đảo
- Tích hợp với hệ thống cảnh báo quốc gia về an ninh mạng

## **12.3. Tính năng bổ sung**
- Chế độ bảo vệ cho người lớn tuổi: Giao diện đơn giản hóa, thông báo đến người thân
- Phân tích email và mạng xã hội để phát hiện lừa đảo đa kênh
- Báo cáo tự động gửi đến cơ quan chức năng khi phát hiện lừa đảo


# **XIII.  KẾT LUẬN**

Đề tài "Ứng dụng cảnh báo lừa đảo qua tin nhắn và cuộc gọi" được xây dựng nhằm giải quyết một vấn đề thực tiễn cấp bách trong xã hội số hiện đại. Với kiến trúc hệ thống rõ ràng, thuật toán phân tích đa tiêu chí và giao diện hướng đến người dùng phổ thông, sản phẩm hứa hẹn mang lại giá trị thực tiễn cao.

Ứng dụng không chỉ là công cụ công nghệ, mà còn đóng vai trò như một "người bảo vệ số" – luôn theo dõi, cảnh báo và bảo vệ người dùng khỏi các hình thức lừa đảo ngày càng tinh vi. Đây là bước đi thiết thực trong hành trình xây dựng một không gian số an toàn, tin cậy hơn cho cộng đồng người dùng Việt Nam.

Nhóm cam kết phát triển sản phẩm với tinh thần trách nhiệm, sáng tạo và hướng đến lợi ích cộng đồng.

**Sinh viên thực hiện:**

**Đào Minh Khương  –  Phí Đình Huynh**

Lớp DCCNTT 13.10.11
Đào Minh Khương  |  Phí Đình Huynh  –  DCCNTT 13.10.11	Trang 
