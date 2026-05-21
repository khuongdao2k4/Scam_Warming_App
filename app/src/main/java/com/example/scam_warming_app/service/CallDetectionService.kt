package com.example.scam_warming_app.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.scam_warming_app.ai.SpeechToTextService
import com.example.scam_warming_app.domain.usecase.AnalyzeCallUseCase
import com.example.scam_warming_app.utils.ContactHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CallDetectionService : Service() {

    @Inject
    lateinit var analyzeCallUseCase: AnalyzeCallUseCase

    @Inject
    lateinit var sttService: SpeechToTextService

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var telephonyManager: TelephonyManager
    private var callStateListener: PhoneStateListener? = null
    private var currentPhoneNumber: String? = null

    companion object {
        private const val NOTIFICATION_ID = 888
        private const val CHANNEL_ID = "call_detection_channel"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        
        // CẬP NHẬT CHO ANDROID 14+: Khai báo cả Microphone và Special Use
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID, 
                createNotification(), 
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE or ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, createNotification())
        }

        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        setupCallListener()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        currentPhoneNumber = intent?.getStringExtra("PHONE_NUMBER")
        return START_STICKY
    }

    private fun setupCallListener() {
        callStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                val targetPhone = currentPhoneNumber ?: phoneNumber ?: "Unknown"

                when (state) {
                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        Log.d("CallService", "Bắt đầu nghe máy. Kích hoạt phân tích STT cho $targetPhone")
                        startSpeechAnalysis(targetPhone)
                    }
                    TelephonyManager.CALL_STATE_IDLE -> {
                        Log.d("CallService", "Kết thúc cuộc gọi. Giải phóng tài nguyên.")
                        stopSpeechAnalysis()
                        stopForeground(STOP_FOREGROUND_REMOVE)
                        stopSelf()
                    }
                }
            }
        }
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    private fun startSpeechAnalysis(phoneNumber: String) {
        serviceScope.launch {
            sttService.startListening().collectLatest { transcript ->
                val result = analyzeCallUseCase(phoneNumber, transcript)
                if (result.isScam) {
                    showWarningOverlay(phoneNumber, result.category ?: "Lừa đảo", result.riskScore)
                }
            }
        }
    }

    private fun stopSpeechAnalysis() {
        sttService.stopListening()
    }

    private fun showWarningOverlay(phone: String, category: String, score: Int) {
        val intent = Intent(this, OverlayService::class.java).apply {
            putExtra(OverlayService.EXTRA_MESSAGE, "⚠ PHÁT HIỆN: $category")
            putExtra(OverlayService.EXTRA_SCORE, score)
            putExtra(OverlayService.EXTRA_PHONE_NUMBER, phone)
            putExtra(OverlayService.EXTRA_IS_SPOOFED, score >= 100)
        }
        startService(intent)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Lá chắn bảo vệ đang chạy")
            .setContentText("Đang giám sát cuộc gọi số lạ để bảo vệ bạn...")
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Call Protection", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        callStateListener?.let { telephonyManager.listen(it, PhoneStateListener.LISTEN_NONE) }
    }
}
