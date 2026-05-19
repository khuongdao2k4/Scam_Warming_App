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
    // DANH SÁCH LINK TẢI TRỰC TIẾP (Hãy thay bằng link GitHub Release của bạn để ổn định 100%)
    private val MODEL_URLS = listOf(
        "https://storage.googleapis.com/mediapipe-models/llm_inference/gemma_2b_it_cpu_int4.bin",
        "https://storage.googleapis.com/mediapipe-assets/gemma_2b_it_cpu_int4.bin",
        "https://huggingface.co/google/gemma-1.1-2b-it-tflite/resolve/main/gemma-1.1-2b-it-cpu-int4.bin"
    )

    fun downloadModel(urlIndex: Int = 0): Flow<DownloadStatus> = flow {
        if (urlIndex >= MODEL_URLS.size) {
            emit(DownloadStatus.Error("Không tìm thấy link tải tự động khả dụng. Vui lòng thử lại sau hoặc chọn file thủ công."))
            return@flow
        }

        val currentUrl = MODEL_URLS[urlIndex]
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        
        val destinationFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "gemma-2b-it-cpu-int4.bin")
        if (destinationFile.exists()) destinationFile.delete()

        val request = try {
            DownloadManager.Request(Uri.parse(currentUrl))
                .setTitle("Đang tự động kích hoạt AI")
                .setDescription("Tải dữ liệu thông minh (1.3GB)...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "gemma-2b-it-cpu-int4.bin")
                .setAllowedOverMetered(true) 
                .setAllowedOverRoaming(true)
        } catch (e: Exception) {
            downloadModel(urlIndex + 1).collect { emit(it) }
            return@flow
        }

        val downloadId = try {
            downloadManager.enqueue(request)
        } catch (e: Exception) {
            emit(DownloadStatus.Error("Lỗi khởi tạo tải: ${e.message}"))
            return@flow
        }

        var isDownloading = true
        emit(DownloadStatus.Downloading(0))

        while (isDownloading) {
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor = downloadManager.query(query)
            if (cursor != null && cursor.moveToFirst()) {
                val statusCol = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                val reasonCol = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                val downloadedCol = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                val totalCol = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)

                if (statusCol != -1 && reasonCol != -1 && downloadedCol != -1 && totalCol != -1) {
                    val status = cursor.getInt(statusCol)
                    val reason = cursor.getInt(reasonCol)
                    val bytesDownloaded = cursor.getLong(downloadedCol)
                    val bytesTotal = cursor.getLong(totalCol)

                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            isDownloading = false
                            val success = gemmaAiEngine.installModelFromDownloads(destinationFile)
                            if (success) emit(DownloadStatus.Success) 
                            else emit(DownloadStatus.Error("Lỗi giải mã mô hình AI"))
                        }
                        DownloadManager.STATUS_FAILED -> {
                            isDownloading = false
                            if (reason == 404 || reason == 401 || reason == 403) {
                                Log.w("DownloadAI", "Link $urlIndex lỗi $reason, thử link dự phòng...")
                                downloadModel(urlIndex + 1).collect { emit(it) }
                                return@flow
                            }
                            emit(DownloadStatus.Error("Tải thất bại (Mã: $reason)"))
                        }
                        DownloadManager.STATUS_RUNNING -> {
                            val progress = if (bytesTotal > 0) (bytesDownloaded * 100 / bytesTotal).toInt() else 0
                            emit(DownloadStatus.Downloading(progress))
                        }
                    }
                }
            }
            cursor?.close()
            if (isDownloading) delay(1500)
        }
    }
}
