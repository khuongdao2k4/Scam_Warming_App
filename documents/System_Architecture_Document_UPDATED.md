**SYSTEM ARCHITECTURE DOCUMENT (CẬP NHẬT THỰC TẾ)**

# **1. Cấu trúc Module Android App**
Hệ thống hiện tại được tổ chức theo mô hình Clean Architecture kết hợp với các Service chạy ngầm:

*   **Receiver Layer:** 
    *   `SmsReceiver`: Bắt SMS, gửi dữ liệu phân tích ngay lập tức.
    *   `CallReceiver`: Nhận diện số lạ, kích hoạt dịch vụ bảo vệ cuộc gọi.
*   **Service Layer:**
    *   `CallDetectionService`: Foreground Service quản lý STT và phân tích hội thoại.
    *   `OverlayService`: Hiển thị UI cảnh báo rủi ro cao nhất.
*   **AI Engine Layer (Hybrid):**
    *   `GemmaAiEngine`: Xử lý NLP offline bằng mô hình 2 tỷ tham số.
    *   `RiskScoreEngine`: Hệ thống chuyên gia dựa trên luật (Keywords/URLs).
    *   `ModelDownloadManager`: Quản lý tải và nạp mô hình thủ công từ bộ nhớ máy.

# **2. Luồng dữ liệu Phân tích SMS (Hybrid Flow)**
1. SMS đến -> `SmsReceiver`.
2. `SmsRepository` kiểm tra kết nối mạng qua `NetworkMonitor`.
3. **Nếu Online:** Gửi Request (Phone, Message, DeviceID) tới FastAPI -> Trả kết quả chuẩn xác cao.
4. **Nếu Offline/Timeout:** 
   - Kích hoạt `RiskScoreEngine` quét từ khóa & URL cục bộ.
   - Gọi `GemmaAiEngine` để hiểu ngữ nghĩa (nếu model đã nạp).
5. Kết hợp điểm (Risk Score) -> Lưu Room DB -> Hiển thị Notification/Overlay.

# **3. Cải tiến Hệ thống AI (Cài đặt thủ công)**
Hệ thống đã bổ sung luồng `installManualModel`:
- Cho phép người dùng bypass trình tải tự động.
- `HomeViewModel` nhận `InputStream` từ File Picker.
- `GemmaAiEngine` copy file vào thư mục nội bộ và nạp trực tiếp vào RAM.

# **4. Chiến lược Bảo mật & Quyền riêng tư**
- Toàn bộ dữ liệu SMS/Call Transcript được xử lý trong RAM, chỉ metadata rủi ro được gửi về server (để bảo vệ cộng đồng).
- Cơ sở dữ liệu local được bảo vệ bởi `SQLCipher`.
