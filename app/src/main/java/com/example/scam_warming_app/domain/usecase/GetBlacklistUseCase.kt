package com.example.scam_warming_app.domain.usecase

import com.example.scam_warming_app.data.local.entity.BlacklistEntity
import com.example.scam_warming_app.domain.repository.IBlacklistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBlacklistUseCase @Inject constructor(
    private val blacklistRepository: IBlacklistRepository
) {
    operator fun invoke(): Flow<List<BlacklistEntity>> {
        return blacklistRepository.getFullBlacklist()
    }
}
