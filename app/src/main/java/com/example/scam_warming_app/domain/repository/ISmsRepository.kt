package com.example.scam_warming_app.domain.repository

import com.example.scam_warming_app.domain.model.AnalysisResult
import com.example.scam_warming_app.data.local.entity.SmsEntity
import kotlinx.coroutines.flow.Flow

interface ISmsRepository {
    suspend fun analyzeSms(phoneNumber: String, message: String): AnalysisResult
    suspend fun saveSms(sms: SmsEntity)
    fun getAllSms(): Flow<List<SmsEntity>>
    suspend fun getSmsById(id: Long): SmsEntity?
    suspend fun deleteSmsHistory()
}
