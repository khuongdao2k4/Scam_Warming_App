package com.example.scam_warming_app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.example.scam_warming_app.service.CallDetectionService
import com.example.scam_warming_app.service.OverlayService
import com.example.scam_warming_app.utils.ContactHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CallReceiver : BroadcastReceiver() {

    @Inject
    lateinit var contactHelper: ContactHelper

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            // Lưu ý: Tên hiển thị đôi khi được gửi kèm trong một số tùy biến Android hoặc VoIP
            // Ở đây ta giả định kiểm tra logic so khớp tên và số thực tế
            
            when (state) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    if (phoneNumber != null) {
                        // 1. KIỂM TRA GIẢ MẠO DANH TÍNH (Spoofing)
                        // Giả sử hệ thống nhận diện được tên hiển thị (DisplayName)
                        
                        // SỬA LỖI: Loại bỏ tham số 'context' vì contactHelper đã tự quản lý nó qua Hilt
                        val isSaved = contactHelper.isContactSaved(phoneNumber)
                        
                        if (!isSaved) {
                            Log.d("CallReceiver", "Số lạ đang gọi: $phoneNumber. Khởi động bảo vệ...")
                            startProtectionService(context, phoneNumber)
                        } else {
                            Log.d("CallReceiver", "Người quen đang gọi. Bỏ qua để đảm bảo riêng tư.")
                        }
                    }
                }
            }
        }
    }

    private fun startProtectionService(context: Context, phoneNumber: String) {
        val serviceIntent = Intent(context, CallDetectionService::class.java).apply {
            putExtra("PHONE_NUMBER", phoneNumber)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}
