package com.example.scam_warming_app.domain.usecase

import com.example.scam_warming_app.data.local.entity.SmsEntity
import com.example.scam_warming_app.domain.model.AnalysisResult
import com.example.scam_warming_app.domain.repository.ISmsRepository
import javax.inject.Inject

class AnalyzeSmsUseCase @Inject constructor(
    private val smsRepository: ISmsRepository
) {
    suspend operator fun invoke(sender: String, message: String): AnalysisResult {
        val result = smsRepository.analyzeSms(sender, message)
        
        // Save to history
        val entity = SmsEntity(
            sender = sender,
            message = message,
            timestamp = System.currentTimeMillis(),
            riskScore = result.riskScore,
            isScam = result.isScam,
            category = result.category,
            reasons = result.reasons.joinToString(", ")
        )
        smsRepository.saveSms(entity)
        
        return result
    }
}
