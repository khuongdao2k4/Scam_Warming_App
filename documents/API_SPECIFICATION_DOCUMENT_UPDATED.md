**API SPECIFICATION DOCUMENT**

**Hệ thống ứng dụng cảnh báo lừa đảo qua tin nhắn và cuộc gọi**

-----
**1. GIỚI THIỆU**

**1.1. Mục đích tài liệu**

Tài liệu API Specification mô tả:

- danh sách API của hệ thống, 
- request/response format, 
- cơ chế xác thực, 
- status code, 
- validation rule, 
- luồng dữ liệu giữa: 
  - Android App, 
  - AI Engine, 
  - FastAPI Backend, 
  - Database. 

Tài liệu này được sử dụng để:

- phát triển Backend FastAPI, 
- kết nối Android App, 
- tích hợp AI Detection, 
- đồng bộ dữ liệu Local và Cloud. 
-----
**1.2. Kiến trúc API**

Hệ thống sử dụng:

- RESTful API 
- JSON Format 
- HTTPS Protocol 
-----
**1.3. Base URL**

**Development**

http://localhost:8000/api

**Production**

https://api.scamwarning.com/api

-----
**1.4. Content Type**

Content-Type: application/json

-----
**1.5. Authentication**

Hệ thống sử dụng:

- JWT Token Authentication 

Header:

Authorization: Bearer <access\_token>

-----
**2. API PHÂN TÍCH SMS**

**2.1. Phân tích SMS**

**Endpoint**

POST /api/analyze/sms

-----
**Mô tả**

API nhận dữ liệu SMS từ Android App và gửi tới AI Engine để:

- phân tích nội dung, 
- phát hiện lừa đảo, 
- tính Risk Score, 
- trả kết quả về ứng dụng. 
-----
**Request Body**

{\
`  `"phone\_number": "0901234567",\
`  `"message": "Tài khoản của bạn đã bị khóa. Vui lòng truy cập link...",\
`  `"received\_time": "2026-05-14T10:30:00",\
`  `"device\_id": "android\_device\_001"\
}

-----
**Request Field Description**

|**Field**|**Type**|**Required**|**Mô tả**|
| :-: | :-: | :-: | :-: |
|phone\_number|String|✔|Số điện thoại gửi SMS|
|message|String|✔|Nội dung SMS|
|received\_time|Datetime|✔|Thời gian nhận|
|device\_id|String|✔|ID thiết bị Android|

-----
**Validation Rules**

|**Field**|**Rule**|
| :-: | :-: |
|phone\_number|Đúng format số điện thoại|
|message|Không được null|
|message|Tối đa 5000 ký tự|

-----
**Response Success**

{\
`  `"success": true,\
`  `"risk\_score": 92,\
`  `"is\_scam": true,\
`  `"category": "OTP Scam",\
`  `"reasons": [\
`    `"Phát hiện từ khóa nguy hiểm",\
`    `"Link giả mạo",\
`    `"Số điện thoại nằm trong blacklist"\
`  `],\
`  `"recommendation": "Block this number"\
}

-----
**Response Fields**

|**Field**|**Type**|**Mô tả**|
| :-: | :-: | :-: |
|risk\_score|Integer|Điểm nguy hiểm|
|is\_scam|Boolean|Có phải lừa đảo không|
|category|String|Loại scam|
|reasons|Array|Danh sách lý do|
|recommendation|String|Gợi ý xử lý|

-----
**2.2. Kiểm tra số điện thoại SMS**

**Endpoint**

GET /api/scam-phone/{phone\_number}

-----
**Mô tả**

Kiểm tra số điện thoại có nằm trong blacklist hay không.

-----
**Example Request**

GET /api/scam-phone/0901234567

-----
**Response**

{\
`  `"exists": true,\
`  `"risk\_level": 95,\
`  `"category": "Bank Scam",\
`  `"report\_count": 120\
}

-----
**3. API PHÂN TÍCH CUỘC GỌI**

**3.1. Phân tích transcript cuộc gọi**

**Endpoint**

POST /api/analyze/call

-----
**Mô tả**

API nhận transcript cuộc gọi sau khi Speech-to-Text hoàn tất.

AI sẽ:

- phân tích nội dung hội thoại, 
- phát hiện lừa đảo, 
- trả Risk Score realtime. 
-----
**Request Body**

{\
`  `"phone\_number": "0909999999",\
`  `"transcript": "Anh chị cần cung cấp OTP để xác minh...",\
`  `"call\_time": "2026-05-14T11:20:00"\
}

-----
**Response**

{\
`  `"risk\_score": 98,\
`  `"is\_scam": true,\
`  `"category": "Fake Bank Call",\
`  `"warning\_message": "Không cung cấp OTP cho người lạ"\
}

-----
**3.2. Gửi audio chunk realtime**

**Endpoint**

POST /api/call/audio-stream

-----
**Mô tả**

Android App gửi audio từng đoạn nhỏ realtime để AI xử lý liên tục.

-----
**Request**

{\
`  `"phone\_number": "0909999999",\
`  `"audio\_chunk": "base64\_encoded\_audio",\
`  `"sequence": 1\
}

-----
**4. API REPORT CỘNG ĐỒNG**

**4.1. Report số điện thoại lừa đảo**

**Endpoint**

POST /api/report

-----
**Mô tả**

Cho phép người dùng gửi báo cáo số điện thoại lừa đảo lên cloud.

-----
**Request**

{\
`  `"phone\_number": "0907777777",\
`  `"report\_type": "Fake Police",\
`  `"description": "Giả danh công an yêu cầu chuyển khoản"\
}

-----
**Response**

{\
`  `"success": true,\
`  `"message": "Report submitted successfully"\
}

-----
**5. API BLACKLIST**

**5.1. Đồng bộ blacklist**

**Endpoint**

GET /api/blacklist

-----
**Mô tả**

Android App tải blacklist mới từ server.

-----
**Response**

{\
`  `"data": [\
`    `{\
`      `"phone\_number": "0901234567",\
`      `"category": "OTP Scam",\
`      `"risk\_level": 95\
`    `}\
`  `]\
}

-----
**5.2. Đồng bộ keyword lừa đảo**

**Endpoint**

GET /api/scam-keywords

-----
**Response**

{\
`  `"keywords": [\
`    `"OTP",\
`    `"chuyển khoản",\
`    `"xác minh tài khoản"\
`  `]\
}

-----
**6. API BLOCK SỐ ĐIỆN THOẠI**

**6.1. Chặn số điện thoại**

**Endpoint**

POST /api/block-number

-----
**Mô tả**

Android App gửi yêu cầu block số điện thoại.

-----
**Request**

{\
`  `"phone\_number": "0901234567",\
`  `"reason": "Detected scam"\
}

-----
**Response**

{\
`  `"success": true,\
`  `"message": "Phone number blocked"\
}

-----
**7. API AI LOGGING**

**7.1. Gửi AI log**

**Endpoint**

POST /api/ai/log

-----
**Mô tả**

Lưu log AI phục vụ:

- thống kê, 
- debug, 
- monitoring. 
-----
**Request**

{\
`  `"source\_type": "SMS",\
`  `"processing\_mode": "ONLINE",\
`  `"risk\_score": 90,\
`  `"processing\_time": 1.5\
}

-----
**8. STATUS CODE**

|**Code**|**Ý nghĩa**|
| :-: | :-: |
|200|Thành công|
|201|Tạo dữ liệu thành công|
|400|Dữ liệu không hợp lệ|
|401|Unauthorized|
|403|Forbidden|
|404|Not Found|
|500|Internal Server Error|

-----
**9. SECURITY**

**9.1. HTTPS**

Toàn bộ API bắt buộc sử dụng HTTPS để:

- mã hóa dữ liệu, 
- chống sniffing, 
- bảo vệ người dùng. 
-----
**9.2. JWT Authentication**

JWT dùng để:

- xác thực thiết bị, 
- chống request giả mạo, 
- quản lý phiên đăng nhập. 
-----
**9.3. Rate Limiting**

Giới hạn request:

- chống spam API, 
- chống DDOS. 

Ví dụ:

100 request / phút

-----
**10. KIẾN TRÚC LUỒNG API**

Android App\
`      `|\
`      `v\
FastAPI Backend\
`      `|\
`      `v\
AI Detection Engine\
`      `|\
`      `v\
MySQL Database

-----
**11. FLOW PHÂN TÍCH SMS**

SMS đến\
`   `|\
Kiểm tra danh bạ\
`   `|\
Số lạ\
`   `|\
Popup hỏi người dùng\
`   `|\
Người dùng đồng ý\
`   `|\
Gửi API analyze/sms\
`   `|\
AI phân tích\
`   `|\
Trả Risk Score\
`   `|\
Hiển thị cảnh báo\
`   `|\
Gợi ý block số

-----
**12. FLOW PHÂN TÍCH CUỘC GỌI**

Cuộc gọi đến\
`    `|\
Kiểm tra danh bạ\
`    `|\
Số lạ\
`    `|\
Hiển thị popup\
`    `|\
Người dùng đồng ý\
`    `|\
Speech-to-Text\
`    `|\
Gửi transcript\
`    `|\
AI phân tích realtime\
`    `|\
Hiển thị overlay cảnh báo\
`    `|\
Đề xuất block số

-----
**13. KẾT LUẬN**

Hệ thống API hiện tại hỗ trợ:

- phân tích SMS, 
- phân tích cuộc gọi, 
- AI realtime detection, 
- blacklist cộng đồng, 
- block số điện thoại, 
- AI logging, 
- đồng bộ dữ liệu cloud. 

API được thiết kế theo hướng:

- dễ mở rộng, 
- tối ưu realtime, 
- bảo mật, 
- phù hợp với FastAPI, 
- hỗ trợ Android App, 
- và hỗ trợ AI Detection Pipeline trong tương lai. 


**14. API XÁC THỰC (AUTHENTICATION)**

Tài liệu gốc có JWT nhưng thiếu các endpoint đăng ký, đăng nhập và refresh token. Phần này bổ sung đầy đủ.

**14.1. Đăng ký tài khoản**

**Endpoint:**

POST /api/auth/register

Mô tả: Đăng ký thiết bị Android và tạo tài khoản mới.

**Request Body:**

{   "phone\_number": "0901234567",

`  `"device\_id": "android\_device\_001",

`  `"device\_model": "Samsung Galaxy A54",

`  `"os\_version": "Android 14"

}

**Request Fields:**

|**Field**|**Type**|**Required**|**Mô tả**|
| - | - | - | - |
|phone\_number|String|✔|Số điện thoại (format VN 10 số)|
|device\_id|String|✔|ID duy nhất của thiết bị Android|
|device\_model|String|✗|Tên model máy|
|os\_version|String|✗|Phiên bản Android|

**Response Success (201):**

{

`  `"success": true,

`  `"user\_id": 1001,

`  `"access\_token": "eyJhbGciOiJIUzI1NiIs...",

`  `"refresh\_token": "dGhpcyBpcyBhIHJlZnJlc2g...",

`  `"expires\_in": 86400

}

**Validation Rules:**

|**Field**|**Rule**|
| - | - |
|phone\_number|Đúng format VN, bắt đầu 0, 10 số|
|phone\_number|Chưa tồn tại trong hệ thống (UNIQUE)|
|device\_id|Không được null hoặc rỗng|

**14.2. Đăng nhập**

**Endpoint:**

POST /api/auth/login

**Request Body:**

{

`  `"phone\_number": "0901234567",

`  `"device\_id": "android\_device\_001"

}

Response Success (200): Tương tự /api/auth/register trả về access\_token, refresh\_token, expires\_in.

**14.3. Refresh Token**

**Endpoint:**

POST /api/auth/refresh

Mô tả: Làm mới access\_token khi hết hạn (expire sau 24h). Refresh token có hiệu lực 30 ngày.

**Request Body:**

{

`  `"refresh\_token": "dGhpcyBpcyBhIHJlZnJlc2g..."

}

**Response Success (200):**

{

`  `"access\_token": "eyJhbGciOiJIUzI1NiIs...",

`  `"expires\_in": 86400

}

**14.4. Đăng xuất**

**Endpoint:**

POST /api/auth/logout

Header: Authorization: Bearer <access\_token>

**Response:**

{

`  `"success": true,

`  `"message": "Logged out successfully"

}

**15. API CÀI ĐẶT NGƯỜI DÙNG (USER SETTINGS)**

Các API này cho phép Android App đọc và lưu cài đặt người dùng lên cloud, đồng bộ giữa các thiết bị.

**15.1. Lấy cài đặt người dùng**

GET /api/user/settings

Header: Authorization: Bearer <access\_token>

**Response Success (200):**

{

`  `"notification\_enabled": true,

`  `"auto\_analyze\_sms": true,

`  `"call\_analysis\_enabled": true,

`  `"risk\_threshold\_notification": 30,

`  `"risk\_threshold\_overlay": 60,

`  `"risk\_threshold\_block\_suggestion": 85,

`  `"offline\_mode": false

}

**15.2. Cập nhật cài đặt người dùng**

PUT /api/user/settings

Request Body: Bất kỳ field nào từ GET /api/user/settings. Chỉ cần gửi field muốn thay đổi.

**Ví dụ:**

{

`  `"risk\_threshold\_overlay": 70,

`  `"call\_analysis\_enabled": false

}

**Response Success (200):**

{

`  `"success": true,

`  `"message": "Settings updated"

}

**16. API QUẢN LÝ SỐ TIN CẬY (TRUSTED NUMBERS)**

Trusted numbers là danh sách số điện thoại người dùng đánh dấu thủ công là an toàn, bổ sung thêm danh bạ.

**16.1. Lấy danh sách số tin cậy**

GET /api/trusted-numbers

**Response Success (200):**

{

`  `"data": [

`    `{

`      `"phone\_number": "0901234567",

`      `"label": "Ba",

`      `"created\_at": "2026-05-01T10:00:00"

`    `}

`  `]

}

**16.2. Thêm số tin cậy**

POST /api/trusted-numbers

**Request Body:**

{

`  `"phone\_number": "0901234567",

`  `"label": "Ba"

}

Response: { "success": true }

**16.3. Xóa số tin cậy**

DELETE /api/trusted-numbers/{phone\_number}

Ví dụ: DELETE /api/trusted-numbers/0901234567

Response: { "success": true }

**17. CẬP NHẬT KẾT LUẬN**

Hệ thống API đã được cập nhật đầy đủ, bao gồm:

•  Phân tích SMS và phân tích cuộc gọi realtime

•  AI Detection với Risk Score Engine

•  Authentication: đăng ký, đăng nhập, refresh token, đăng xuất

•  Quản lý cài đặt người dùng (User Settings)

•  Quản lý số tin cậy (Trusted Numbers)

•  Blacklist cộng đồng và đồng bộ keyword

•  Block số điện thoại, AI logging, report cộng đồng

