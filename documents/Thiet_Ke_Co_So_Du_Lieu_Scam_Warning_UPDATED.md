**TÀI LIỆU THIẾT KẾ CƠ SỞ DỮ LIỆU**

**Ứng dụng cảnh báo lừa đảo qua tin nhắn và cuộc gọi**

\
Tài liệu mô tả thiết kế cơ sở dữ liệu cho hệ thống ứng dụng cảnh báo lừa đảo qua SMS và cuộc gọi.\
Hệ thống sử dụng mô hình Hybrid Storage kết hợp Local Database và Cloud Database.
# **1. Bảng users**
Lưu thông tin người dùng.

|Tên trường|Kiểu dữ liệu|Ràng buộc|Mô tả|
| :- | :- | :- | :- |
|id|BIGINT|PK, AUTO\_INCREMENT|Mã người dùng|
|full\_name|VARCHAR(100)|NULL|Họ tên|
|phone\_number|VARCHAR(20)|UNIQUE|Số điện thoại|
|email|VARCHAR(100)|NULL|Email|
|created\_at|DATETIME|NOT NULL|Ngày tạo|
# **2. Bảng scam\_phones**
Danh sách số điện thoại lừa đảo.

|Tên trường|Kiểu dữ liệu|Ràng buộc|Mô tả|
| :- | :- | :- | :- |
|id|BIGINT|PK, AUTO\_INCREMENT|Mã dữ liệu|
|phone\_number|VARCHAR(20)|UNIQUE|Số điện thoại|
|category|VARCHAR(50)|NOT NULL|Loại lừa đảo|
|risk\_level|INT|DEFAULT 0|Mức độ nguy hiểm|
|report\_count|INT|DEFAULT 0|Số lượt báo cáo|
|created\_at|DATETIME|NOT NULL|Ngày tạo|
# **3. Bảng scam\_keywords**
Lưu từ khóa lừa đảo.

|Tên trường|Kiểu dữ liệu|Ràng buộc|Mô tả|
| :- | :- | :- | :- |
|id|BIGINT|PK, AUTO\_INCREMENT|Mã từ khóa|
|keyword|VARCHAR(100)|NOT NULL|Từ khóa|
|weight|INT|DEFAULT 0|Trọng số|
|category|VARCHAR(50)|NULL|Nhóm lừa đảo|
|created\_at|DATETIME|NOT NULL|Ngày tạo|
# **4. Bảng scam\_patterns**
Lưu mẫu hành vi lừa đảo.

|Tên trường|Kiểu dữ liệu|Ràng buộc|Mô tả|
| :- | :- | :- | :- |
|id|BIGINT|PK, AUTO\_INCREMENT|Mã pattern|
|pattern\_name|VARCHAR(100)|NOT NULL|Tên pattern|
|description|TEXT|NULL|Mô tả|
|weight|INT|DEFAULT 0|Điểm trọng số|
|created\_at|DATETIME|NOT NULL|Ngày tạo|
# **5. Bảng sms\_history**
Lưu lịch sử SMS phân tích trên local.

|Tên trường|Kiểu dữ liệu|Ràng buộc|Mô tả|
| :- | :- | :- | :- |
|id|BIGINT|PK, AUTO\_INCREMENT|Mã lịch sử|
|phone\_number|VARCHAR(20)|NOT NULL|Số điện thoại|
|content|TEXT|NOT NULL|Nội dung SMS|
|risk\_score|INT|DEFAULT 0|Điểm nguy hiểm|
|result|VARCHAR(20)|NULL|Kết quả|
|is\_scam|BOOLEAN|DEFAULT FALSE|Đánh dấu lừa đảo|
|created\_at|DATETIME|NOT NULL|Thời gian|
# **6. Bảng call\_history**
Lưu lịch sử cuộc gọi phân tích.

|Tên trường|Kiểu dữ liệu|Ràng buộc|Mô tả|
| :- | :- | :- | :- |
|id|BIGINT|PK, AUTO\_INCREMENT|Mã lịch sử|
|phone\_number|VARCHAR(20)|NOT NULL|Số điện thoại|
|transcript|TEXT|NULL|Nội dung thoại|
|risk\_score|INT|DEFAULT 0|Điểm nguy hiểm|
|result|VARCHAR(20)|NULL|Kết quả|
|is\_scam|BOOLEAN|DEFAULT FALSE|Đánh dấu lừa đảo|
|created\_at|DATETIME|NOT NULL|Thời gian|
# **7. Bảng reports**
Lưu báo cáo cộng đồng.

|Tên trường|Kiểu dữ liệu|Ràng buộc|Mô tả|
| :- | :- | :- | :- |
|id|BIGINT|PK, AUTO\_INCREMENT|Mã báo cáo|
|phone\_number|VARCHAR(20)|NOT NULL|Số bị report|
|report\_type|VARCHAR(50)|NOT NULL|Loại lừa đảo|
|description|TEXT|NULL|Mô tả|
|reported\_by|BIGINT|FK -> users.id|Người báo cáo|
|created\_at|DATETIME|NOT NULL|Ngày tạo|
# **8. Bảng blocked\_numbers**
Lưu số điện thoại bị chặn.

|Tên trường|Kiểu dữ liệu|Ràng buộc|Mô tả|
| :- | :- | :- | :- |
|id|BIGINT|PK, AUTO\_INCREMENT|Mã chặn|
|phone\_number|VARCHAR(20)|UNIQUE|Số điện thoại|
|reason|VARCHAR(100)|NULL|Lý do|
|blocked\_at|DATETIME|NOT NULL|Ngày chặn|
# **9. Bảng urls\_blacklist**
Lưu URL độc hại.

|Tên trường|Kiểu dữ liệu|Ràng buộc|Mô tả|
| :- | :- | :- | :- |
|id|BIGINT|PK, AUTO\_INCREMENT|Mã URL|
|url|VARCHAR(255)|UNIQUE|Đường dẫn|
|risk\_level|INT|DEFAULT 0|Mức độ nguy hiểm|
|created\_at|DATETIME|NOT NULL|Ngày tạo|
# **10. Quan hệ giữa các bảng**
• users (1) —— (N) reports

• scam\_phones (1) —— (N) reports

• sms\_history (1) —— (1) ai\_logs

• call\_history (1) —— (1) ai\_logs
# **11. Chiến lược lưu trữ Hybrid**

|Dữ liệu|Local|Cloud|
| :- | :- | :- |
|Full SMS|✔|✘|
|Audio cuộc gọi|✔|✘|
|Transcript|✔|✘|
|Blacklist|✔|✔|
|Metadata lừa đảo|✔|✔|
# **12. Chỉ mục (Index)**
• Index phone\_number trong bảng scam\_phones

• Index phone\_number trong bảng sms\_history

• Index phone\_number trong bảng call\_history

• Index keyword trong bảng scam\_keywords

• Index url trong bảng urls\_blacklist
# **13. Kết luận**

Thiết kế cơ sở dữ liệu được tối ưu cho hệ thống Hybrid AI Detection,\
hỗ trợ phân tích thời gian thực, lưu trữ bảo mật và khả năng mở rộng trong tương lai.

# **4. QUAN HỆ GIỮA CÁC BẢNG (DATABASE RELATIONSHIPS)**
Hệ thống ứng dụng cảnh báo lừa đảo sử dụng mô hình cơ sở dữ liệu quan hệ (Relational Database Model) nhằm đảm bảo:

- tính toàn vẹn dữ liệu, 
- khả năng mở rộng, 
- tối ưu truy vấn, 
- hỗ trợ AI Detection, 
- và đồng bộ dữ liệu giữa Local Database và Cloud Database. 

Các bảng trong hệ thống được liên kết với nhau bằng:

- Primary Key (PK) 
- Foreign Key (FK) 

Mỗi quan hệ đều có vai trò riêng trong việc phục vụ:

- phân tích AI, 
- lưu lịch sử, 
- xây dựng blacklist cộng đồng, 
- và quản lý dữ liệu người dùng. 
-----
# **4.1. Quan hệ giữa bảng users và reports**
## **Kiểu quan hệ**
users (1) -------- (N) reports
## **Mô tả chi tiết**
Bảng users lưu thông tin người dùng của ứng dụng.\
Bảng reports lưu các báo cáo số điện thoại lừa đảo do người dùng gửi lên hệ thống.

Quan hệ:

- Một người dùng có thể gửi nhiều báo cáo khác nhau. 
- Mỗi báo cáo chỉ thuộc về một người dùng duy nhất. 

Ví dụ:

- Người dùng A có thể báo cáo: 
  - số giả danh công an, 
  - số lừa OTP, 
  - số spam đầu tư tài chính. 

Do đó:

users.id → reports.reported\_by

-----
## **Mục đích của quan hệ này**
Quan hệ này giúp hệ thống:

- xác định ai là người gửi báo cáo, 
- tránh spam report, 
- thống kê mức độ đóng góp cộng đồng, 
- đánh giá độ uy tín của báo cáo. 

Ngoài ra hệ thống còn có thể:

- giới hạn số lần report, 
- phát hiện report ảo, 
- tăng độ tin cậy AI. 
-----
## **Khóa liên kết**

|**Bảng**|**Trường**|
| :-: | :-: |
|users|id (PK)|
|reports|reported\_by (FK)|

-----
# **4.2. Quan hệ giữa bảng scam\_phones và reports**
## **Kiểu quan hệ**
scam\_phones (1) -------- (N) reports
## **Mô tả chi tiết**
Bảng scam\_phones lưu danh sách số điện thoại có dấu hiệu lừa đảo.

Bảng reports lưu các báo cáo cộng đồng.

Một số điện thoại lừa đảo có thể bị nhiều người báo cáo khác nhau.

Ví dụ:

090xxxxxxx

có thể bị:

- 20 người báo cáo giả danh ngân hàng, 
- 15 người báo cáo lừa OTP, 
- 10 người báo cáo spam đầu tư. 

Do đó:

- Một số điện thoại sẽ có nhiều bản ghi report. 
- Mỗi report chỉ liên quan đến một số điện thoại. 
-----
## **Vai trò đối với AI**
Quan hệ này cực kỳ quan trọng vì:

- AI có thể dựa vào số lượt report để tăng Risk Score. 
- Hệ thống blacklist cộng đồng được xây dựng từ dữ liệu này. 
- Giúp AI học các mẫu hành vi lừa đảo phổ biến. 

Ví dụ:

- số có 100 report → Risk Score tăng mạnh, 
- số mới xuất hiện → AI đánh giá thấp hơn. 
-----
## **Khóa liên kết**

|**Bảng**|**Trường**|
| :-: | :-: |
|scam\_phones|phone\_number|
|reports|phone\_number|

-----
# **4.3. Quan hệ giữa bảng sms\_history và scam\_phones**
## **Kiểu quan hệ**
sms\_history (N) -------- (1) scam\_phones
## **Mô tả chi tiết**
Bảng sms\_history lưu lịch sử các tin nhắn SMS đã được phân tích.

Bảng scam\_phones lưu danh sách số điện thoại nguy hiểm.

Một số điện thoại lừa đảo có thể gửi rất nhiều SMS khác nhau tới nhiều người dùng.

Ví dụ:

090xxxxxxx

có thể gửi:

- SMS giả mạo ngân hàng, 
- SMS chứa link độc hại, 
- SMS yêu cầu chuyển khoản. 

Do đó:

- Một số lừa đảo sẽ xuất hiện trong nhiều bản ghi SMS history. 
- Mỗi SMS chỉ thuộc về một số điện thoại cụ thể. 
-----
## **Mục đích**
Quan hệ này giúp:

- theo dõi lịch sử spam, 
- phân tích tần suất gửi SMS, 
- phát hiện chiến dịch spam hàng loạt, 
- hỗ trợ AI phân tích hành vi theo thời gian. 

AI có thể học:

- số gửi bao nhiêu SMS/ngày, 
- nội dung có lặp lại hay không, 
- thời gian spam nhiều nhất. 
-----
## **Khóa liên kết**

|**Bảng**|**Trường**|
| :-: | :-: |
|sms\_history|phone\_number|
|scam\_phones|phone\_number|

-----
# **4.4. Quan hệ giữa bảng call\_history và scam\_phones**
## **Kiểu quan hệ**
call\_history (N) -------- (1) scam\_phones
## **Mô tả chi tiết**
Bảng call\_history lưu lịch sử các cuộc gọi đã được phân tích.

Một số điện thoại lừa đảo có thể thực hiện nhiều cuộc gọi tới nhiều người dùng khác nhau.

Ví dụ:

090xxxxxxx

có thể:

- gọi giả danh công an, 
- gọi lừa OTP, 
- gọi yêu cầu chuyển khoản, 
- gọi giả danh nhân viên ngân hàng. 
-----
## **Vai trò đối với AI**
Quan hệ này giúp AI:

- phân tích tần suất cuộc gọi, 
- nhận diện spam call, 
- phát hiện caller ID spoofing, 
- học mẫu hội thoại lừa đảo. 

Ví dụ:

- một số gọi 500 lần/ngày, 
- nhiều transcript giống nhau, 
- nhiều người report cùng nội dung. 

AI sẽ tăng mức độ nguy hiểm của số điện thoại đó.

-----
## **Khóa liên kết**

|**Bảng**|**Trường**|
| :-: | :-: |
|call\_history|phone\_number|
|scam\_phones|phone\_number|

-----
# **4.5. Quan hệ giữa sms\_history và ai\_logs**
## **Kiểu quan hệ**
sms\_history (1) -------- (1) ai\_logs
## **Mô tả chi tiết**
Mỗi lần AI phân tích một SMS:

- hệ thống sẽ sinh ra một AI Log. 

AI Log lưu:

- thời gian xử lý, 
- chế độ AI, 
- Risk Score, 
- trạng thái AI. 

Ví dụ:

- SMS A → 1 AI log, 
- SMS B → 1 AI log. 
-----
## **Vai trò**
Quan hệ này giúp:

- debug AI, 
- đo hiệu năng, 
- thống kê tốc độ xử lý, 
- tối ưu AI pipeline. 
-----
# **4.6. Quan hệ giữa call\_history và ai\_logs**
## **Kiểu quan hệ**
call\_history (1) -------- (1) ai\_logs
## **Mô tả chi tiết**
Mỗi cuộc gọi sau khi phân tích:

- hệ thống sinh một AI log. 

AI log ghi lại:

- thời gian xử lý speech-to-text, 
- thời gian AI inference, 
- Risk Score, 
- mô hình AI được sử dụng. 
-----
## **Mục đích**
Quan hệ này hỗ trợ:

- tối ưu AI realtime, 
- debug lỗi AI, 
- đo hiệu năng hệ thống, 
- thống kê online/offline AI. 
-----
# **4.7. MÔ HÌNH ERD (ENTITY RELATIONSHIP DIAGRAM)**
users\
`   `|\
`   `| 1 ----- N\
`   `|\
reports\
`   `|\
`   `| N ----- 1\
`   `|\
scam\_phones\
`      `^\
`      `|\
`      `| 1\
`      `|\
`      `N\
sms\_history\
\
scam\_phones\
`      `^\
`      `|\
`      `| 1\
`      `|\
`      `N\
call\_history\
\
sms\_history ----- 1 : 1 ----- ai\_logs\
\
call\_history ---- 1 : 1 ----- ai\_logs

-----
# **4.8. Ý NGHĨA THIẾT KẾ QUAN HỆ**
Thiết kế quan hệ hiện tại giúp hệ thống:

- dễ mở rộng, 
- giảm trùng lặp dữ liệu, 
- tối ưu AI, 
- dễ đồng bộ cloud, 
- hỗ trợ realtime detection. 

Ngoài ra còn giúp:

- tăng tốc truy vấn, 
- giảm tải backend, 
- hỗ trợ Room Database, 
- hỗ trợ MySQL production, 
- hỗ trợ AI analytics trong tương lai. 


**PHẦN BỔ SUNG — CÁC BẢNG CÒN THIẾU**

Phần này bổ sung 3 bảng bị thiếu trong tài liệu gốc: ai\_logs (được nhắc đến trong Android Technical Architecture nhưng chưa có schema), trusted\_numbers và user\_settings (cần thiết cho các chức năng trong FRS).

**Bảng ai\_logs — Log phân tích AI**

Lưu kết quả và metadata của mỗi lần AI phân tích SMS hoặc cuộc gọi. Bảng này được nhắc đến trong Android Technical Architecture Document nhưng chưa có schema trong tài liệu gốc.

|**Tên trường**|**Kiểu dữ liệu**|**Ràng buộc**|**Mô tả**|
| :- | :- | :- | :- |
|id|BIGINT|PK, AUTO\_INCREMENT|Mã log|
|source\_type|ENUM('SMS','CALL')|NOT NULL|Loại nguồn phân tích|
|phone\_number|VARCHAR(20)|NOT NULL|Số điện thoại liên quan|
|processing\_mode|ENUM('ONLINE','OFFLINE')|NOT NULL|Chế độ xử lý AI|
|risk\_score|INT|DEFAULT 0|Điểm rủi ro kết quả (0-100)|
|is\_scam|BOOLEAN|DEFAULT FALSE|Kết quả phân tích cuối|
|category|VARCHAR(50)|NULL|Loại scam phát hiện|
|keyword\_matched|TEXT|NULL|JSON array các từ khóa phát hiện|
|processing\_time\_ms|INT|NULL|Thời gian xử lý (milliseconds)|
|model\_version|VARCHAR(20)|NULL|Phiên bản AI model đã dùng|
|sms\_history\_id|BIGINT|FK -> sms\_history.id NULL|Liên kết SMS tương ứng|
|call\_history\_id|BIGINT|FK -> call\_history.id NULL|Liên kết cuộc gọi tương ứng|
|created\_at|DATETIME|NOT NULL|Thời gian tạo log|

**Lưu ý thiết kế:**

•  Chỉ một trong hai FK (sms\_history\_id hoặc call\_history\_id) có giá trị, cái còn lại là NULL.

•  keyword\_matched lưu dạng JSON array: ["OTP", "chuyển khoản", "tài khoản bị khóa"]

•  Index trên (source\_type, created\_at) để query thống kê theo loại và thời gian.

**Index:**

•  INDEX idx\_ai\_logs\_phone ON ai\_logs(phone\_number)

•  INDEX idx\_ai\_logs\_type\_date ON ai\_logs(source\_type, created\_at)

•  INDEX idx\_ai\_logs\_sms ON ai\_logs(sms\_history\_id)

•  INDEX idx\_ai\_logs\_call ON ai\_logs(call\_history\_id)

**Bảng trusted\_numbers — Số điện thoại tin cậy**

Lưu danh sách số điện thoại mà người dùng đánh dấu thủ công là tin cậy, bổ sung thêm số trong danh bạ. Khi gặp số này, hệ thống bỏ qua phân tích AI.

|**Tên trường**|**Kiểu dữ liệu**|**Ràng buộc**|**Mô tả**|
| :- | :- | :- | :- |
|id|BIGINT|PK, AUTO\_INCREMENT|Mã bản ghi|
|user\_id|BIGINT|FK -> users.id NOT NULL|Người dùng sở hữu|
|phone\_number|VARCHAR(20)|NOT NULL|Số điện thoại tin cậy|
|label|VARCHAR(50)|NULL|Nhãn gợi nhớ (Ba, Mẹ, Công ty...)|
|created\_at|DATETIME|NOT NULL|Ngày thêm|

**Ràng buộc:**

•  UNIQUE KEY (user\_id, phone\_number): mỗi user chỉ thêm một số một lần.

•  ON DELETE CASCADE từ users: xóa user thì xóa hết trusted\_numbers của user đó.

**Index:**

•  INDEX idx\_trusted\_user ON trusted\_numbers(user\_id)

•  INDEX idx\_trusted\_phone ON trusted\_numbers(phone\_number)

**Bảng user\_settings — Cài đặt người dùng**

Lưu cài đặt cá nhân của từng người dùng, đồng bộ giữa app và cloud. Quan hệ One-to-One với bảng users.

|**Tên trường**|**Kiểu dữ liệu**|**Ràng buộc**|**Mô tả**|
| :- | :- | :- | :- |
|id|BIGINT|PK, AUTO\_INCREMENT|Mã cài đặt|
|user\_id|BIGINT|FK -> users.id UNIQUE|Người dùng (1-1)|
|notification\_enabled|BOOLEAN|DEFAULT TRUE|Bật thông báo|
|auto\_analyze\_sms|BOOLEAN|DEFAULT TRUE|Tự động phân tích SMS|
|call\_analysis\_enabled|BOOLEAN|DEFAULT TRUE|Bật phân tích cuộc gọi|
|risk\_threshold\_notify|INT|DEFAULT 30|Ngưỡng hiển thị thông báo (0-100)|
|risk\_threshold\_overlay|INT|DEFAULT 60|Ngưỡng hiển thị overlay (0-100)|
|risk\_threshold\_block|INT|DEFAULT 85|Ngưỡng gợi ý chặn số (0-100)|
|offline\_mode|BOOLEAN|DEFAULT FALSE|Ưu tiên AI offline|
|updated\_at|DATETIME|NOT NULL|Lần cập nhật cuối|

**Lưu ý thiết kế:**

•  Tạo tự động khi user đăng ký: INSERT INTO user\_settings (user\_id, updated\_at) VALUES (?, NOW())

•  Constraint: risk\_threshold\_notify < risk\_threshold\_overlay < risk\_threshold\_block

**Cập nhật quan hệ giữa các bảng (bổ sung)**

|**Quan hệ**|**Kiểu**|**Ghi chú**|
| :- | :- | :- |
|users (1) — (N) trusted\_numbers|One-to-Many|ON DELETE CASCADE|
|users (1) — (1) user\_settings|One-to-One|Tạo cùng lúc với user|
|sms\_history (1) — (0..1) ai\_logs|One-to-One optional|ai\_logs.sms\_history\_id FK|
|call\_history (1) — (0..1) ai\_logs|One-to-One optional|ai\_logs.call\_history\_id FK|
|users (1) — (N) sms\_history|One-to-Many|Thêm FK user\_id vào sms\_history|
|users (1) — (N) call\_history|One-to-Many|Thêm FK user\_id vào call\_history|

**Cập nhật chiến lược lưu trữ Hybrid (bổ sung các bảng mới)**

|**Dữ liệu**|**Local (SQLite)**|**Cloud (MySQL)**|**Lý do**|
| :- | :- | :- | :- |
|ai\_logs|✔|✔|Local: truy vấn nhanh. Cloud: analytics, debug|
|trusted\_numbers|✔|✔|Đồng bộ giữa nhiều thiết bị|
|user\_settings|✔|✔|Đồng bộ khi đổi thiết bị|
|sms\_history|✔ (full)|✘|Privacy: không upload nội dung SMS|
|call\_history|✔ (full)|✘|Privacy: không upload transcript|

**Schema Room Database (Android Local — bổ sung)**

Các bảng Local tương ứng trong Room Database Android cần được thêm vào:

**AiLogEntity.kt:**

@Entity(tableName = "ai\_logs")

data class AiLogEntity(

`    `@PrimaryKey(autoGenerate = true) val id: Long = 0,

`    `val sourceType: String,

`    `val phoneNumber: String,

`    `val processingMode: String,

`    `val riskScore: Int,

`    `val isScam: Boolean,

`    `val category: String?,

`    `val keywordMatched: String?,

`    `val processingTimeMs: Int?,

`    `val smsHistoryId: Long?,

`    `val callHistoryId: Long?,

`    `val createdAt: Long = System.currentTimeMillis()

)

