package com.example.scam_warming_app.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.example.scam_warming_app.ai.GemmaAiEngine
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelDownloadManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gemmaAiEngine: GemmaAiEngine
) {
    private val MODEL_URLS = listOf(
        "https://github.com/khuongdao2k4/Scam_Warming_App/releases/download/M%C3%B4_h%C3%ACnh_Gemma/gemma-2b-it-cpu-int4.bin",
        "https://storage.googleapis.com/mediapipe-models/llm_inference/gemma_2b_it_cpu_int4.bin"
    )

    fun downloadModel(urlIndex: Int = 0): Flow<DownloadStatus> = flow {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        
        // 1. Kiểm tra xem có tiến trình nào đang chạy dở không để "bắt nhịp" lại
        val existingId = findExistingDownloadId(downloadManager)
        var downloadId: Long = existingId ?: -1L

        if (existingId == null) {
            if (urlIndex >= MODEL_URLS.size) {
                emit(DownloadStatus.Error("Tất cả link đều lỗi. Vui lòng chọn file thủ công."))
                return@flow
            }

            val destinationFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "gemma-2b-it-cpu-int4.bin")
            if (destinationFile.exists()) destinationFile.delete()

            val request = try {
                DownloadManager.Request(Uri.parse(MODEL_URLS[urlIndex]))
                    .setTitle("Kích hoạt bộ não AI")
                    .setDescription("Đang tải dữ liệu trí tuệ nhân tạo (1.3GB)...")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                    .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "gemma-2b-it-cpu-int4.bin")
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
            } catch (e: Exception) {
                downloadModel(urlIndex + 1).collect { emit(it) }
                return@flow
            }
            downloadId = downloadManager.enqueue(request)
        }

        // 2. Theo dõi tiến trình với cơ chế kiên nhẫn
        var isDownloading = true
        var errorCount = 0

        while (isDownloading) {
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor = downloadManager.query(query)
            if (cursor != null && cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                val reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON))
                val downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                when (status) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        isDownloading = false
                        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "gemma-2b-it-cpu-int4.bin")
                        if (gemmaAiEngine.installModelFromDownloads(file)) emit(DownloadStatus.Success)
                        else emit(DownloadStatus.Error("Lỗi giải mã mô hình."))
                    }
                    DownloadManager.STATUS_FAILED -> {
                        // Nếu lỗi 1006 hoặc 404, thử link tiếp theo thay vì báo lỗi đỏ ngay
                        if ((reason == 1006 || reason == 404) && urlIndex < MODEL_URLS.size - 1) {
                            isDownloading = false
                            downloadModel(urlIndex + 1).collect { emit(it) }
                            return@flow
                        }
                        
                        // Cho phép hệ thống tự thử lại 3 lần trước khi báo lỗi thực sự
                        errorCount++
                        if (errorCount > 3) {
                            isDownloading = false
                            emit(DownloadStatus.Error("Tải thất bại (Mã: $reason). Thử lại sau."))
                        } else {
                            emit(DownloadStatus.Downloading(-1)) // Hiện trạng thái "Đang kết nối lại..."
                            delay(5000) 
                        }
                    }
                    DownloadManager.STATUS_RUNNING -> {
                        val progress = if (total > 0) (downloaded * 100 / total).toInt() else 0
                        emit(DownloadStatus.Downloading(progress))
                    }
                    DownloadManager.STATUS_PAUSED, DownloadManager.STATUS_PENDING -> {
                        emit(DownloadStatus.Downloading(-1))
                    }
                }
            } else {
                isDownloading = false
            }
            cursor?.close()
            if (isDownloading) delay(2000)
        }
    }

    private fun findExistingDownloadId(manager: DownloadManager): Long? {
        val query = DownloadManager.Query().setFilterByStatus(
            DownloadManager.STATUS_RUNNING or DownloadManager.STATUS_PAUSED or DownloadManager.STATUS_PENDING
        )
        val cursor = manager.query(query)
        var foundId: Long? = null
        if (cursor != null) {
            val titleIdx = cursor.getColumnIndex(DownloadManager.COLUMN_TITLE)
            val idIdx = cursor.getColumnIndex(DownloadManager.COLUMN_ID)
            while (cursor.moveToNext()) {
                if (titleIdx != -1 && idIdx != -1 && cursor.getString(titleIdx) == "Kích hoạt bộ não AI") {
                    foundId = cursor.getLong(idIdx)
                    break
                }
            }
            cursor.close()
        }
        return foundId
    }
}
