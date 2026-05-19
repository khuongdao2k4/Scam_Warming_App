package com.example.scam_warming_app.ai

import android.content.Context
import android.util.Log
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

/**
 * Công cụ phân tích AI Gemma. 
 */
class GemmaAiEngine(private val context: Context) {
    private var llmInference: LlmInference? = null
    private val initializationMutex = Mutex()
    
    private val internalModelFile by lazy { File(context.filesDir, "gemma-2b-it-cpu-int4.bin") }

    private suspend fun ensureModelLoaded(): LlmInference? = initializationMutex.withLock {
        if (llmInference != null) return llmInference

        if (internalModelFile.exists()) {
            return try {
                Log.d("GemmaAI", "Đang nạp bộ não AI từ: ${internalModelFile.absolutePath}")
                val options = LlmInference.LlmInferenceOptions.builder()
                    .setModelPath(internalModelFile.absolutePath)
                    .setMaxTokens(512)
                    .setTemperature(0.7f)
                    .build()
                llmInference = LlmInference.createFromOptions(context, options)
                llmInference
            } catch (e: Exception) {
                Log.e("GemmaAI", "Lỗi nạp mô hình: ${e.message}")
                null
            }
        }
        return null
    }

    suspend fun analyzeText(prompt: String): String = withContext(Dispatchers.IO) {
        val engine = ensureModelLoaded() ?: return@withContext "AI Offline (Chưa cài đặt mô hình)"
        
        return@withContext try {
            val systemPrompt = "Hãy phân tích nội dung này và trả lời xem có dấu hiệu lừa đảo không (ngắn gọn): "
            engine.generateResponse(systemPrompt + prompt)
        } catch (e: Exception) {
            "Lỗi phân tích: ${e.message}"
        }
    }

    fun isModelLoaded(): Boolean = internalModelFile.exists()

    // Cài đặt từ file đã tải thủ công
    suspend fun installModelFromStream(inputStream: InputStream): Boolean = withContext(Dispatchers.IO) {
        try {
            // Tạm thời đóng inference nếu đang mở
            llmInference?.close()
            llmInference = null
            
            internalModelFile.outputStream().use { output ->
                inputStream.use { input ->
                    val buffer = ByteArray(8 * 1024)
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                    }
                }
            }
            Log.d("GemmaAI", "Cài đặt file thủ công thành công")
            true
        } catch (e: Exception) {
            Log.e("GemmaAI", "Lỗi cài đặt từ stream: ${e.message}")
            false
        }
    }

    fun installModelFromDownloads(downloadedFile: File? = null): Boolean {
        return try {
            val sourceFile = downloadedFile ?: File(
                android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS),
                "gemma-2b-it-cpu-int4.bin"
            )
            
            if (sourceFile.exists()) {
                sourceFile.copyTo(internalModelFile, overwrite = true)
                if (downloadedFile != null) sourceFile.delete()
                true
            } else false
        } catch (e: Exception) {
            false
        }
    }
}
