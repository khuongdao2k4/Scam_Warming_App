package com.example.scam_warming_app.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.scam_warming_app.R
import com.example.scam_warming_app.domain.usecase.BlockNumberUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OverlayService : Service() {

    @Inject
    lateinit var blockNumberUseCase: BlockNumberUseCase

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {
        const val EXTRA_MESSAGE = "extra_message"
        const val EXTRA_SCORE = "extra_score"
        const val EXTRA_IS_SPOOFED = "extra_is_spoofed"
        const val EXTRA_PHONE_NUMBER = "extra_phone_number"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val message = intent?.getStringExtra(EXTRA_MESSAGE) ?: "Phát hiện dấu hiệu lừa đảo!"
        val score = intent?.getIntOf(EXTRA_SCORE, 0) ?: 0
        val isSpoofed = intent?.getBooleanOf(EXTRA_IS_SPOOFED, false) ?: false
        val phoneNumber = intent?.getStringExtra(EXTRA_PHONE_NUMBER) ?: ""
        
        showOverlay(message, score, isSpoofed, phoneNumber)
        return START_NOT_STICKY
    }

    private fun showOverlay(message: String, score: Int, isSpoofed: Boolean, phoneNumber: String) {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        
        val layoutParams = WindowManager.LayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            format = PixelFormat.TRANSLUCENT
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.TOP
            y = 100
        }

        overlayView = LayoutInflater.from(this).inflate(R.layout.layout_warning_overlay, null)
        
        val container = overlayView?.findViewById<LinearLayout>(R.id.rootLayout)
        val title = overlayView?.findViewById<TextView>(R.id.tvWarningTitle)
        
        if (isSpoofed) {
            container?.setBackgroundColor(Color.parseColor("#B71C1C"))
            title?.text = "⚠ CẢNH BÁO GIẢ MẠO"
        }

        overlayView?.findViewById<TextView>(R.id.tvWarningMessage)?.text = message
        overlayView?.findViewById<TextView>(R.id.tvRiskScore)?.text = if (isSpoofed) "Mức độ: Cực kỳ nguy hiểm" else "Độ rủi ro: $score%"
        
        overlayView?.findViewById<Button>(R.id.btnBlockNumber)?.setOnClickListener {
            if (phoneNumber.isNotEmpty()) {
                serviceScope.launch {
                    val result = blockNumberUseCase(phoneNumber)
                    if (result.isSuccess) {
                        Toast.makeText(this@OverlayService, "Đã chặn số: $phoneNumber", Toast.LENGTH_SHORT).show()
                        stopSelf()
                    } else {
                        Toast.makeText(this@OverlayService, "Không thể chặn số hệ thống", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Không xác định được số điện thoại", Toast.LENGTH_SHORT).show()
            }
        }

        overlayView?.findViewById<Button>(R.id.btnCloseOverlay)?.setOnClickListener {
            stopSelf()
        }

        windowManager?.addView(overlayView, layoutParams)
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayView?.let {
            windowManager?.removeView(it)
        }
    }
    
    private fun Intent.getIntOf(key: String, default: Int): Int = getIntExtra(key, default)
    private fun Intent.getBooleanOf(key: String, default: Boolean): Boolean = getBooleanExtra(key, default)
}
