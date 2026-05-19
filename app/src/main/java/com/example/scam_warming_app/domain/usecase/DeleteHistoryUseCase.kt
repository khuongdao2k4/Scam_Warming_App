package com.example.scam_warming_app.domain.usecase

import com.example.scam_warming_app.domain.repository.ICallRepository
import com.example.scam_warming_app.domain.repository.ISmsRepository
import javax.inject.Inject

class DeleteHistoryUseCase @Inject constructor(
    private val smsRepository: ISmsRepository,
    private val callRepository: ICallRepository
) {
    suspend operator fun invoke() {
        smsRepository.deleteSmsHistory()
        callRepository.deleteCallHistory()
    }
}
