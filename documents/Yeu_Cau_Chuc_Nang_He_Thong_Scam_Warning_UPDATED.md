**TÀI LIỆU YÊU CẦU CHỨC NĂNG HỆ THỐNG (CẬP NHẬT THEO THỰC TẾ)**

**Hệ thống cảnh báo lừa đảo Scam Warning v1.5**

Tài liệu này phản ánh các chức năng đã được triển khai và cải tiến trong quá trình phát triển thực tế, bao gồm cơ chế Hybrid AI và quản lý mô hình học máy cục bộ.

# **1. Chức năng quản lý và phân tích SMS**
## **FR-SMS-01 — Phát hiện và Phân tích tự động**
*   **Mô tả:** Hệ thống tự động phát hiện SMS đến từ số lạ và thực hiện phân tích ngay lập tức để bảo vệ người dùng theo thời gian thực.
*   **Cải tiến:** Thay vì hỏi người dùng có muốn kiểm tra không (như thiết kế cũ), hệ thống sẽ tự động phân tích ngầm và chỉ hiển thị cảnh báo nếu phát hiện rủi ro cao, giúp giảm phiền hà.
*   **Luồng xử lý:**
    1. Nhận SMS qua `SmsReceiver`.
    2. Kiểm tra danh bạ (Trusted Contacts).
    3. Nếu là số lạ, gửi nội dung đến `AnalyzeSmsUseCase`.

## **FR-SMS-02 — Cơ chế Phân tích Hybrid AI (Cải tiến mới)**
*   **Online Mode (Ưu tiên):** Gửi nội dung đầy đủ lên FastAPI để tận dụng sức mạnh của Gemini/Groq AI, trả về kết quả chính xác cao với lý do chi tiết.
*   **Offline Fallback:** Nếu mất mạng hoặc API timeout (>5s), hệ thống tự động chuyển sang `LocalAIEngine` (RiskScore + Gemma-2B) để đảm bảo không bỏ sót tin nhắn độc hại.

## **FR-SMS-03 — Hiển thị Cảnh báo Thông minh**
*   **Mức độ:**
    *   **An toàn:** Không hiển thị UI, lưu nhật ký âm thầm.
    *   **Nghi ngờ:** Hiển thị Notification màu vàng.
    *   **Nguy hiểm/Lừa đảo:** Hiển thị Overlay đỏ nổi bật với lý do cụ thể (VD: "Phát hiện giả mạo ngân hàng").

# **2. Chức năng quản lý và phân tích cuộc gọi**
## **FR-CALL-01 — Giám sát cuộc gọi số lạ**
*   **Mô tả:** Tự động kích hoạt lớp bảo vệ khi nhận cuộc gọi từ số không có trong danh bạ.
*   **Cải tiến:** Tích hợp `CallDetectionService` chạy dưới dạng Foreground Service chuyên dụng để đảm bảo tính ổn định trên Android 13+.

## **FR-CALL-02 — Chuyển đổi giọng nói thời gian thực (STT)**
*   **Công nghệ:** Sử dụng Hybrid STT (Google Speech-to-Text khi online và Vosk khi offline).
*   **Xử lý:** Âm thanh được chia thành các đoạn (chunks) 5 giây để phân tích liên tục, giúp đưa ra cảnh báo ngay trong khi đang đàm thoại.

## **FR-CALL-03 — Cảnh báo Overlay & Chặn số**
*   **Giao diện:** Overlay hiển thị điểm rủi ro (%) và các từ khóa vi phạm.
*   **Hành động:** Nút "Chặn số" cho phép người dùng đưa số điện thoại vào danh sách đen của hệ thống (BlockedNumbers API) chỉ với 1 lần chạm.

# **3. Chức năng quản lý mô hình AI (Cải tiến mới)**
## **FR-AI-01 — Tải mô hình tự động**
*   Người dùng có thể theo dõi tiến trình tải xuống mô hình Gemma-2B ngay trên màn hình chính.
*   Hỗ trợ Resume Download nếu kết nối mạng bị gián đoạn.

## **FR-AI-02 — Cài đặt mô hình thủ công**
*   **Mô tả:** Cho phép người dùng chọn file mô hình (`.bin` hoặc `.gguf`) từ bộ nhớ máy.
*   **Mục đích:** Hỗ trợ người dùng ở khu vực mạng yếu hoặc muốn cập nhật các phiên bản AI tùy chỉnh.

# **4. Chức năng lưu trữ và Đồng bộ**
## **FR-DATA-01 — Nhật ký bảo vệ tập trung**
*   Lưu trữ chi tiết tin nhắn, transcript cuộc gọi, điểm rủi ro và nguồn phân tích (Online/Offline) vào Room Database.
*   Chức năng "Xóa hết" nhật ký để đảm bảo riêng tư tuyệt đối khi người dùng yêu cầu.

## **FR-DATA-02 — Đồng bộ hóa cộng đồng**
*   **Blacklist Sync:** Tự động cập nhật danh sách số điện thoại lừa đảo từ server mỗi 6 giờ qua WorkManager.
*   **AI Log Sync:** Gửi metadata (không gửi nội dung nhạy cảm) về server để huấn luyện mô hình AI tốt hơn.

# **5. Quyền riêng tư và Bảo mật**
*   **Mã hóa:** Sử dụng `SQLCipher` để mã hóa toàn bộ cơ sở dữ liệu Room cục bộ.
*   **Minimization:** Chỉ lưu nội dung tin nhắn/cuộc gọi trên thiết bị, tuyệt đối không lưu trữ nội dung hội thoại trên Cloud (chỉ lưu metadata phân tích).
*   **Quyền hạn:** Sử dụng Runtime Permissions và hướng dẫn người dùng cấp quyền `SYSTEM_ALERT_WINDOW` một cách minh bạch.
