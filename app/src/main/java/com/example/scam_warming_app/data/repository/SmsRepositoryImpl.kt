package com.example.scam_warming_app.data.repository

import android.content.Context
import android.provider.Settings
import com.example.scam_warming_app.ai.GemmaAiEngine
import com.example.scam_warming_app.ai.RiskScoreEngine
import com.example.scam_warming_app.data.local.dao.SmsDao
import com.example.scam_warming_app.data.local.entity.SmsEntity
import com.example.scam_warming_app.data.remote.ApiService
import com.example.scam_warming_app.data.remote.SmsAnalysisRequest
import com.example.scam_warming_app.domain.model.AnalysisResult
import com.example.scam_warming_app.domain.repository.ISmsRepository
import com.example.scam_warming_app.domain.usecase.LogAiResultUseCase
import com.example.scam_warming_app.utils.NetworkMonitor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val smsDao: SmsDao,
    private val apiService: ApiService,
    private val riskScoreEngine: RiskScoreEngine,
    private val gemmaAiEngine: GemmaAiEngine,
    private val networkMonitor: NetworkMonitor,
    private val logAiResultUseCase: LogAiResultUseCase
) : ISmsRepository {

    override suspend fun analyzeSms(phoneNumber: String, message: String): AnalysisResult {
        val startTime = System.currentTimeMillis()
        val isOnline = networkMonitor.isOnline.first()
        
        // Sử dụng context để lấy Device ID
        val deviceId = try {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
        
        val result = if (isOnline) {
            try {
                val response = apiService.analyzeSms(
                    SmsAnalysisRequest(
                        phone_number = phoneNumber,
                        message = message,
                        device_id = deviceId
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
                localAnalysis(phoneNumber, message)
            }
        } else {
            localAnalysis(phoneNumber, message)
        }

        val processingTime = (System.currentTimeMillis() - startTime) / 1000.0
        logAiResultUseCase("SMS", result, processingTime)
        
        return result
    }

    private suspend fun localAnalysis(phone: String, msg: String): AnalysisResult {
        val ruleResult = riskScoreEngine.calculateRiskScore(phone, msg)
        
        val aiOpinion = if (gemmaAiEngine.isModelLoaded()) {
            gemmaAiEngine.analyzeText(msg)
        } else {
            null
        }

        val reasons = ruleResult.matchedKeywords.toMutableList()
        aiOpinion?.let { reasons.add("AI Gemma: $it") }

        val isScamByAi = aiOpinion?.lowercase()?.contains("lừa đảo") == true

        return AnalysisResult(
            riskScore = if (isScamByAi) 95 else ruleResult.score,
            isScam = ruleResult.isScam || isScamByAi,
            category = ruleResult.category,
            reasons = reasons,
            recommendation = if (ruleResult.isScam || isScamByAi) "Cảnh báo nội dung nguy hiểm!" else "An toàn.",
            isOfflineMode = true
        )
    }

    override suspend fun saveSms(sms: SmsEntity) {
        smsDao.insertSms(sms)
    }

    override fun getAllSms(): Flow<List<SmsEntity>> = smsDao.getAllSms()
    override suspend fun getSmsById(id: Long): SmsEntity? = smsDao.getSmsById(id)
    override suspend fun deleteSmsHistory() = smsDao.deleteAll()
}
