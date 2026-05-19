package com.example.scam_warming_app.domain.usecase

import com.example.scam_warming_app.data.local.entity.CallEntity
import com.example.scam_warming_app.domain.model.AnalysisResult
import com.example.scam_warming_app.domain.repository.ICallRepository
import javax.inject.Inject

class AnalyzeCallUseCase @Inject constructor(
    private val callRepository: ICallRepository
) {
    suspend operator fun invoke(phoneNumber: String, transcript: String): AnalysisResult {
        val result = callRepository.analyzeCallTranscript(phoneNumber, transcript)
        
        // Save call result
        val entity = CallEntity(
            phoneNumber = phoneNumber,
            timestamp = System.currentTimeMillis(),
            riskScore = result.riskScore,
            isScam = result.isScam,
            category = result.category,
            transcript = transcript
        )
        callRepository.saveCall(entity)
        
        return result
    }
}
