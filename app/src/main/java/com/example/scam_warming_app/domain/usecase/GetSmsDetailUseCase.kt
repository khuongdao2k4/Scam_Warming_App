package com.example.scam_warming_app.domain.usecase

import com.example.scam_warming_app.data.local.entity.SmsEntity
import com.example.scam_warming_app.domain.repository.ISmsRepository
import javax.inject.Inject

class GetSmsDetailUseCase @Inject constructor(
    private val smsRepository: ISmsRepository
) {
    suspend operator fun invoke(id: Long): SmsEntity? {
        return smsRepository.getSmsById(id)
    }
}
