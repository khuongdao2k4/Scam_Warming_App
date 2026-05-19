package com.example.scam_warming_app.receiver

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import com.example.scam_warming_app.ai.GemmaAiEngine
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class DownloadCompletedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var gemmaAiEngine: GemmaAiEngine

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            if (id == -1L) return

            Log.d("DownloadReceiver", "Hệ thống báo tải file AI hoàn tất (ID: $id)")
            
            // Kiểm tra file trong thư mục Private của App (nơi DownloadManager lưu về)
            context?.let { ctx ->
                val destinationFile = File(ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "gemma-2b-it-cpu-int4.bin")
                
                if (destinationFile.exists()) {
                    Log.d("DownloadReceiver", "Tìm thấy file, đang tự động kích hoạt AI ngầm...")
                    val success = gemmaAiEngine.installModelFromDownloads(destinationFile)
                    if (success) {
                        Log.d("DownloadReceiver", "Kích hoạt AI thành công ngay cả khi App đang tắt!")
                    }
                }
            }
        }
    }
}
