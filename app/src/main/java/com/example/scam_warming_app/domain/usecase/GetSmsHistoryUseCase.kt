package com.example.scam_warming_app.domain.usecase

import com.example.scam_warming_app.data.local.entity.SmsEntity
import com.example.scam_warming_app.domain.repository.ISmsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSmsHistoryUseCase @Inject constructor(
    private val smsRepository: ISmsRepository
) {
    operator fun invoke(): Flow<List<SmsEntity>> {
        return smsRepository.getAllSms()
    }
}
