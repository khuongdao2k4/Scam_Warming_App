package com.example.scam_warming_app.domain.usecase

import com.example.scam_warming_app.data.local.entity.CallEntity
import com.example.scam_warming_app.domain.repository.ICallRepository
import javax.inject.Inject

class GetCallDetailUseCase @Inject constructor(
    private val callRepository: ICallRepository
) {
    suspend operator fun invoke(id: Long): CallEntity? {
        return callRepository.getCallById(id)
    }
}
