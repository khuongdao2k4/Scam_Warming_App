package com.example.scam_warming_app.domain.usecase

import com.example.scam_warming_app.data.local.entity.CallEntity
import com.example.scam_warming_app.domain.repository.ICallRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCallHistoryUseCase @Inject constructor(
    private val callRepository: ICallRepository
) {
    operator fun invoke(): Flow<List<CallEntity>> {
        return callRepository.getAllCalls()
    }
}
