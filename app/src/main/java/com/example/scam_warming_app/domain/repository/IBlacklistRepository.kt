package com.example.scam_warming_app.domain.repository

import com.example.scam_warming_app.data.local.entity.BlacklistEntity
import kotlinx.coroutines.flow.Flow

interface IBlacklistRepository {
    fun getFullBlacklist(): Flow<List<BlacklistEntity>>
    suspend fun syncBlacklist(): Result<Unit>
    suspend fun isNumberBlacklisted(phoneNumber: String): BlacklistEntity?
}
