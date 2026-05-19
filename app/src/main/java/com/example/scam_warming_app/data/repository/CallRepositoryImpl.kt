package com.example.scam_warming_app.data.repository

import com.example.scam_warming_app.ai.GemmaAiEngine
import com.example.scam_warming_app.ai.RiskScoreEngine
import com.example.scam_warming_app.data.local.dao.CallDao
import com.example.scam_warming_app.data.local.entity.CallEntity
import com.example.scam_warming_app.data.remote.ApiService
import com.example.scam_warming_app.data.remote.CallAnalysisRequest
import com.example.scam_warming_app.domain.model.AnalysisResult
import com.example.scam_warming_app.domain.repository.ICallRepository
import com.example.scam_warming_app.domain.usecase.LogAiResultUseCase
import com.example.scam_warming_app.utils.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class CallRepositoryImpl @Inject constructor(
    private val callDao: CallDao,
    private val apiService: ApiService,
    private val riskScoreEngine: RiskScoreEngine,
    private val gemmaAiEngine: GemmaAiEngine,
    private val networkMonitor: NetworkMonitor,
    private val logAiResultUseCase: LogAiResultUseCase
) : ICallRepository {

    override suspend fun analyzeCallTranscript(
        phoneNumber: String, 
        transcript: String
    ): AnalysisResult {
        val startTime = System.currentTimeMillis()
        val isOnline = networkMonitor.isOnline.first()
        
        val result = if (isOnline) {
            try {
                val response = apiService.analyzeCall(
                    CallAnalysisRequest(
                        phone_number = phoneNumber,
                        transcript = transcript,
                        call_time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
                    )
                )
                AnalysisResult(
                    riskScore = response.risk_score,
                    isScam = response.is_scam,
                    category = response.category,
                    reasons = response.reasons ?: emptyList(),
                    recommendation = response.warning_message,
                    isOfflineMode = false
                )
            } catch (e: Exception) {
                localAnalysis(phoneNumber, transcript)
            }
        } else {
            localAnalysis(phoneNumber, transcript)
        }

        // Ghi nhật ký hiệu năng AI cho cuộc gọi
        val processingTime = (System.currentTimeMillis() - startTime) / 1000.0
        logAiResultUseCase("CALL", result, processingTime)
        
        return result
    }

    private suspend fun localAnalysis(phone: String, transcript: String): AnalysisResult {
        // Gọi bộ não AI với logic phân tích quy tắc và nhận diện spoofing
        val ruleResult = riskScoreEngine.calculateRiskScore(phone, transcript, isSpoofed = false)
        
        val aiOpinion = if (gemmaAiEngine.isModelLoaded()) {
            gemmaAiEngine.analyzeText(transcript)
        } else {
            null
        }

        val reasons = ruleResult.matchedKeywords.toMutableList()
        aiOpinion?.let { reasons.add("AI Gemma nhận định: $it") }

        return AnalysisResult(
            riskScore = ruleResult.score,
            isScam = ruleResult.isScam || (aiOpinion?.contains("lừa đảo", ignoreCase = true) == true),
            category = ruleResult.category,
            reasons = reasons,
            recommendation = if (ruleResult.isScam) "HÃY CÚP MÁY NGAY!" else "Đang giám sát an toàn.",
            isOfflineMode = true
        )
    }

    override suspend fun saveCall(call: CallEntity) {
        callDao.insertCall(call)
    }

    override fun getAllCalls(): Flow<List<CallEntity>> = callDao.getAllCalls()

    override suspend fun getCallById(id: Long): CallEntity? = callDao.getCallById(id)

    override suspend fun deleteCallHistory() = callDao.deleteAll()
}
