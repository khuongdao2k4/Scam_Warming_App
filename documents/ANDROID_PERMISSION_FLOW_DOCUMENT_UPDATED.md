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


**BỔ SUNG — ANDROID 13+ RUNTIME PERMISSION HANDLING**

Tài liệu gốc thiếu phần xử lý permission đặc thù của Android 13 (API 33) và Android 14 (API 34). Từ Android 13 trở đi có nhiều thay đổi quan trọng ảnh hưởng trực tiếp đến app Scam Warning.

**1. Những thay đổi Permission từ Android 13+**

|**Permission**|**Android ≤ 12**|**Android 13+**|**Tác động với Scam Warning**|
| - | - | - | - |
|POST\_NOTIFICATIONS|Không cần xin|Phải xin runtime|PHẢI xin trước khi show notification cảnh báo|
|READ\_MEDIA\_IMAGES|READ\_EXTERNAL\_STORAGE|Tách riêng|Không ảnh hưởng (app không đọc ảnh)|
|NEARBY\_WIFI\_DEVICES|ACCESS\_FINE\_LOCATION|Tách riêng|Không ảnh hưởng|
|RECEIVE\_SMS|Runtime từ API 23|Không đổi|Vẫn phải xin như cũ|
|RECORD\_AUDIO|Runtime từ API 23|Không đổi|Vẫn phải xin như cũ|
|READ\_PHONE\_STATE|Runtime từ API 23|Không đổi|Vẫn phải xin như cũ|

**2. Android 14 — Thay đổi quan trọng về SCHEDULE\_EXACT\_ALARM**

Từ Android 14 (API 34), permission SCHEDULE\_EXACT\_ALARM bị thu hồi mặc định. App cần xử lý fallback sang setAndAllowWhileIdle() nếu không có exact alarm.

**3. Runtime Flow: POST\_NOTIFICATIONS (Android 13+)**

Đây là thay đổi QUAN TRỌNG nhất vì app Scam Warning phụ thuộc hoàn toàn vào notification để cảnh báo người dùng.

**Thời điểm xin permission:**

•  Xin ngay sau khi user hoàn thành onboarding (không xin ở màn hình đầu tiên).

•  Giải thích lý do trước khi xin: "App cần gửi thông báo để cảnh báo khi phát hiện lừa đảo."

•  Nếu bị từ chối lần 1: hiển thị dialog giải thích thêm, xin lại một lần.

•  Nếu bị từ chối lần 2 (shouldShowRequestPermissionRationale = false): hướng dẫn vào Settings.

**Code xử lý Android 13+ POST\_NOTIFICATIONS:**

// PermissionManager.kt

fun requestNotificationPermission(activity: AppCompatActivity) {

`    `if (Build.VERSION.SDK\_INT >= Build.VERSION\_CODES.TIRAMISU) {

`        `when {

`            `ContextCompat.checkSelfPermission(

`                `activity, Manifest.permission.POST\_NOTIFICATIONS

`            `) == PackageManager.PERMISSION\_GRANTED -> {

`                `// Đã có permission — tiếp tục bình thường

`            `}

`            `activity.shouldShowRequestPermissionRationale(

`                `Manifest.permission.POST\_NOTIFICATIONS

`            `) -> {

`                `// Hiển thị dialog giải thích rồi mới xin

`                `showNotificationRationaleDialog(activity)

`            `}

`            `else -> {

`                `// Xin permission trực tiếp

`                `requestPermissionLauncher.launch(

`                    `Manifest.permission.POST\_NOTIFICATIONS

`                `)

`            `}

`        `}

`    `}

`    `// Android < 13: không cần xin, notification mặc định được

}

**4. Hành vi Fallback khi Permission bị từ chối**

|**Permission bị từ chối**|**Tính năng ảnh hưởng**|**Fallback behavior**|**Hiển thị cho user**|
| - | - | - | - |
|POST\_NOTIFICATIONS|Cảnh báo realtime|Ghi log local, không push notification. User mở app mới thấy lịch sử.|Banner nhắc nhở ở HomeScreen|
|RECEIVE\_SMS|Phân tích nội dung SMS|Chỉ log số điện thoại, không phân tích text. Overlay vẫn hiện nhưng giảm độ chính xác.|Warning badge ở Settings|
|RECORD\_AUDIO|STT phân tích cuộc gọi|Tắt hoàn toàn Call Analysis. Chỉ dùng caller ID detection.|Card giải thích + link Settings|
|READ\_CONTACTS|Filter trusted contacts|Phân tích TẤT CẢ số lạ, tăng false positive.|Thông báo nhẹ trong Settings|
|SYSTEM\_ALERT\_WINDOW|Overlay cảnh báo cuộc gọi|Dùng Full-screen Notification Intent thay thế (Android 10+).|Hướng dẫn bật lại trong Settings|

**5. Thứ tự xin Permission tối ưu (Updated)**

Thứ tự xin permission ảnh hưởng lớn đến tỷ lệ chấp nhận của người dùng. Xin theo thứ tự từ ít nhạy cảm đến nhạy cảm:

|**Bước**|**Permission**|**Thời điểm**|**Lý do giải thích cho user**|
| - | - | - | - |
|1|POST\_NOTIFICATIONS (Android 13+)|Sau onboarding|"Để gửi cảnh báo lừa đảo kịp thời"|
|2|READ\_PHONE\_STATE|Sau onboarding|"Để nhận diện số gọi đến"|
|3|READ\_CONTACTS|Sau onboarding|"Để bỏ qua số trong danh bạ của bạn"|
|4|RECEIVE\_SMS|Lần dùng đầu|"Để phân tích nội dung SMS lừa đảo"|
|5|RECORD\_AUDIO|Khi bật Call Analysis|"Để phân tích tiếng nói kẻ lừa đảo trong cuộc gọi"|
|6|SYSTEM\_ALERT\_WINDOW|Khi setup hoàn tất|"Để hiện cảnh báo ngay trên màn hình khi đang gọi"|

**6. AndroidManifest.xml — Khai báo Permission đầy đủ**

Khai báo đầy đủ trong AndroidManifest.xml, phân chia theo API level:

<!-- Core permissions — tất cả API level -->

<uses-permission android:name="android.permission.INTERNET" />

<uses-permission android:name="android.permission.RECEIVE\_SMS" />

<uses-permission android:name="android.permission.READ\_SMS" />

<uses-permission android:name="android.permission.READ\_PHONE\_STATE" />

<uses-permission android:name="android.permission.READ\_CONTACTS" />

<uses-permission android:name="android.permission.RECORD\_AUDIO" />

<uses-permission android:name="android.permission.FOREGROUND\_SERVICE" />

<uses-permission android:name="android.permission.SYSTEM\_ALERT\_WINDOW" />

<uses-permission android:name="android.permission.RECEIVE\_BOOT\_COMPLETED" />

<!-- Android 9+ -->

<uses-permission android:name="android.permission.FOREGROUND\_SERVICE\_MICROPHONE"

`    `android:minSdkVersion="34" />

<!-- Android 13+ (API 33) -->

<uses-permission android:name="android.permission.POST\_NOTIFICATIONS"

`    `android:minSdkVersion="33" />

<!-- Android 14+ (API 34) -->

<uses-permission android:name="android.permission.USE\_EXACT\_ALARM"

`    `android:minSdkVersion="34" />

**7. Permission State Machine — Vòng đời Permission**

|**Trạng thái**|**shouldShowRationale()**|**Hành động tiếp theo**|
| - | - | - |
|GRANTED|N/A|Tiếp tục bình thường|
|DENIED lần 1|true|Hiển thị Rationale Dialog → xin lại|
|DENIED lần 2 (Permanently)|false|Mở Settings hướng dẫn bật tay|
|DENIED + Don't ask again|false|Mở Settings, hiển thị banner nhắc nhở|

**8. PermissionManager.kt — Cấu trúc class**

Class tập trung quản lý tất cả permission, tránh logic phân tán:

class PermissionManager(private val activity: AppCompatActivity) {

`    `// Kiểm tra trạng thái tất cả permission cần thiết

`    `fun checkAllPermissions(): PermissionStatus

`    `// Xin từng permission theo thứ tự ưu tiên

`    `fun requestCorePermissions(callback: (Boolean) -> Unit)

`    `// Android 13+ notification permission

`    `fun requestNotificationPermission(callback: (Boolean) -> Unit)

`    `// Xin SYSTEM\_ALERT\_WINDOW (cần mở Settings riêng)

`    `fun requestOverlayPermission()

`    `// Mở Settings app cho permission cụ thể

`    `fun openAppSettings(permission: String)

`    `// Kiểm tra có cần giải thích trước khi xin không

`    `fun shouldShowRationale(permission: String): Boolean

}

