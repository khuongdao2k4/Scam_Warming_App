package com.example.scam_warming_app.domain.usecase

import com.example.scam_warming_app.domain.repository.IBlacklistRepository
import javax.inject.Inject

class SyncBlacklistUseCase @Inject constructor(
    private val blacklistRepository: IBlacklistRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return blacklistRepository.syncBlacklist()
    }
}
