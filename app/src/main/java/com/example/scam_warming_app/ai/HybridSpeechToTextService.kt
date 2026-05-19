package com.example.scam_warming_app.ai

import android.util.Log
import com.example.scam_warming_app.utils.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class HybridSpeechToTextService @Inject constructor(
    private val voskService: VoskSpeechToTextService,
    private val googleService: GoogleSpeechToTextService,
    private val networkMonitor: NetworkMonitor
) : SpeechToTextService {

    private val _transcriptFlow = MutableSharedFlow<String>()
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var activeJob: Job? = null
    private var lastResultTime = 0L

    override fun startListening(): Flow<String> {
        activeJob?.cancel()
        activeJob = scope.launch {
            // Bước 1: Khởi động Vosk (Ưu tiên quyền riêng tư)
            Log.d("HybridSTT", "Starting with Vosk (Offline)...")
            launch {
                voskService.startListening().collect { result ->
                    lastResultTime = System.currentTimeMillis()
                    _transcriptFlow.emit(result)
                }
            }

            // Bước 2: Theo dõi hiệu quả của Vosk
            delay(5000) // Đợi 5 giây đầu cuộc gọi
            
            while (true) {
                val isOnline = networkMonitor.isOnline.first()
                val timeSinceLastResult = System.currentTimeMillis() - lastResultTime

                // Nếu Vosk im lặng quá lâu (> 7s) và có mạng -> Cầu cứu Google
                if (isOnline && (timeSinceLastResult > 7000 || lastResultTime == 0L)) {
                    Log.d("HybridSTT", "Vosk might be struggling. Switching to Google STT (Online)...")
                    voskService.stopListening()
                    
                    try {
                        googleService.startListening().collect { result ->
                            _transcriptFlow.emit(result)
                        }
                    } catch (e: Exception) {
                        Log.e("HybridSTT", "Google STT failed, falling back to Vosk", e)
                        // Nếu Google lỗi, quay lại thử Vosk
                        launch {
                            voskService.startListening().collect { _transcriptFlow.emit(it) }
                        }
                    }
                    break // Đã chuyển sang Google thành công
                }
                delay(3000) // Kiểm tra lại sau mỗi 3 giây
            }
        }
        return _transcriptFlow.asSharedFlow()
    }

    override fun stopListening() {
        activeJob?.cancel()
        voskService.stopListening()
        googleService.stopListening()
    }

    override fun isModelReady(): Boolean = voskService.isModelReady()
}
