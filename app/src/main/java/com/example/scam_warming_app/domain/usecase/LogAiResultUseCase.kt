package com.example.scam_warming_app.domain.usecase

import com.example.scam_warming_app.data.local.dao.AiLogDao
import com.example.scam_warming_app.data.local.entity.AiLogEntity
import com.example.scam_warming_app.data.remote.ApiService
import com.example.scam_warming_app.data.remote.AiLogRequest
import com.example.scam_warming_app.domain.model.AnalysisResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class LogAiResultUseCase @Inject constructor(
    private val apiService: ApiService,
    private val aiLogDao: AiLogDao
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    suspend operator fun invoke(sourceType: String, result: AnalysisResult, processingTime: Double) {
        // 1. Lưu log cục bộ (Local)
        val logEntity = AiLogEntity(
            sourceType = sourceType,
            processingMode = if (result.isOfflineMode) "OFFLINE" else "ONLINE",
            riskScore = result.riskScore,
            processingTime = processingTime
        )
        aiLogDao.insertLog(logEntity)

        // 2. Gửi log lên Cloud (Nếu có mạng)
        scope.launch {
            try {
                apiService.logAiResult(
                    AiLogRequest(
                        source_type = sourceType,
                        processing_mode = logEntity.processingMode,
                        risk_score = result.riskScore,
                        processing_time = processingTime
                    )
                )
            } catch (e: Exception) {
                // Thất bại khi gửi lên cloud không được gây ảnh hưởng ứng dụng
            }
        }
    }
}
