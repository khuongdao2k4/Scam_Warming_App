**ANDROID PERMISSION FLOW DOCUMENT**

**Hệ thống ứng dụng cảnh báo lừa đảo qua tin nhắn và cuộc gọi**

-----
**1. GIỚI THIỆU**

**1.1. Mục đích**

Tài liệu này mô tả:

- toàn bộ permission Android, 
- thời điểm xin quyền, 
- luồng xử lý permission, 
- fallback khi user từ chối quyền. 
-----
**1.2. Mục tiêu**

- tránh crash app, 
- tối ưu UX, 
- hỗ trợ Android 13/14, 
- hỗ trợ realtime detection. 
-----
**2. DANH SÁCH PERMISSION**

|**Permission**|**Mục đích**|
| :-: | :-: |
|RECEIVE\_SMS|Nhận SMS|
|READ\_SMS|Đọc nội dung SMS|
|READ\_CONTACTS|Kiểm tra danh bạ|
|READ\_CALL\_LOG|Đọc lịch sử gọi|
|READ\_PHONE\_STATE|Detect call|
|RECORD\_AUDIO|Speech-to-Text|
|SYSTEM\_ALERT\_WINDOW|Overlay|
|POST\_NOTIFICATIONS|Notification|
|FOREGROUND\_SERVICE|Realtime service|
|INTERNET|API connection|

-----
**3. PERMISSION FLOW TỔNG QUÁT**

App Start\
`    `|\
Permission Setup Screen\
`    `|\
Request Permissions\
`    `|\
+------------------------+\
| Granted                |\
|                        |\
| Continue App           |\
|                        |\
+------------------------+\
|\
| Denied\
|\
Show Explanation\
|\
Request Again

-----
**4. SMS PERMISSION FLOW**

**4.1. RECEIVE\_SMS**

**Mục đích**

Cho phép app:

- nhận SMS realtime. 
-----
**Flow**

Request RECEIVE\_SMS\
`      `|\
Granted?\
`  `+---+---+\
`  `| YES   | NO\
`  `|       |\
Enable    Show Warning\
SMS AI    Limited Feature

-----
**4.2. READ\_SMS**

**Dùng để**

- đọc nội dung SMS, 
- AI phân tích. 
-----
**Nếu từ chối**

App:

- không phân tích SMS, 
- chỉ detect số điện thoại. 
-----
**5. CONTACTS PERMISSION FLOW**

**5.1. READ\_CONTACTS**

**Mục đích**

Bỏ qua:

- số quen, 
- trusted contacts. 
-----
**Flow**

Request READ\_CONTACTS\
`      `|\
Granted?\
`  `+---+---+\
`  `| YES   | NO\
`  `|       |\
Enable    Analyze all numbers\
Trusted\
Filter

-----
**6. CALL PERMISSION FLOW**

**6.1. READ\_PHONE\_STATE**

**Dùng để**

- detect incoming call, 
- detect call state. 
-----
**6.2. READ\_CALL\_LOG**

**Dùng để**

- lưu lịch sử, 
- AI analytics. 
-----
**7. MICROPHONE PERMISSION FLOW**

**7.1. RECORD\_AUDIO**

**Mục đích**

Speech-to-Text realtime.

-----
**Flow**

Enable Call Detection\
`        `|\
Request RECORD\_AUDIO\
`        `|\
Granted?\
`   `+----+----+\
`   `| YES     | NO\
`   `|         |\
Enable       Disable\
Speech AI    Realtime Analysis

-----
**8. OVERLAY PERMISSION FLOW**

**8.1. SYSTEM\_ALERT\_WINDOW**

**Dùng để**

- hiển thị warning overlay, 
- popup realtime. 
-----
**Flow**

Enable Overlay\
`      `|\
Open Android Settings\
`      `|\
Grant Overlay Permission\
`      `|\
Overlay Service Active

-----
**9. NOTIFICATION PERMISSION FLOW**

**9.1. POST\_NOTIFICATIONS**

**Android 13+**

Bắt buộc xin quyền notification.

-----
**Dùng để**

- warning popup, 
- scam alert, 
- background service notification. 
-----
**10. FOREGROUND SERVICE FLOW**

**10.1. FOREGROUND\_SERVICE**

**Dùng cho**

- realtime detection, 
- overlay service, 
- speech processing. 
-----
**11. PERMISSION FALLBACK LOGIC**

|**Permission bị từ chối**|**Fallback**|
| :-: | :-: |
|RECEIVE\_SMS|Disable SMS AI|
|READ\_CONTACTS|Analyze all numbers|
|RECORD\_AUDIO|Disable call AI|
|SYSTEM\_ALERT\_WINDOW|Use notification only|

-----
**12. PERMISSION UX RULES**

**12.1. Không xin toàn bộ quyền cùng lúc**

Permission được chia theo:

- feature-based request, 
- contextual request. 
-----
**12.2. Explain Before Request**

Ví dụ:

Ứng dụng cần quyền microphone để phát hiện cuộc gọi lừa đảo realtime.

-----
**13. ANDROID VERSION COMPATIBILITY**

|**Android Version**|**Lưu ý**|
| :-: | :-: |
|Android 10|Background limits|
|Android 11|Overlay restriction|
|Android 12|Foreground service limits|
|Android 13|Notification permission|
|Android 14|Microphone background restriction|

-----
**14. SECURITY RULES**

|**Quy tắc**|**Mục đích**|
| :-: | :-: |
|Least Permission|Chỉ xin quyền cần thiết|
|Local Processing|Không upload audio|
|User Consent|Luôn hỏi người dùng|
|Permission Transparency|Giải thích rõ|

-----
**15. KẾT LUẬN**

Permission Architecture hiện tại hỗ trợ:

- realtime SMS detection, 
- realtime call detection, 
- overlay warning, 
- speech-to-text, 
- Android 13/14 compatibility, 
- privacy-first design, 
- fallback processing, 
- stable background services.

