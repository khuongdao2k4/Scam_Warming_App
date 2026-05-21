package com.example.scam_warming_app.data.repository

import com.example.scam_warming_app.data.local.dao.BlacklistDao
import com.example.scam_warming_app.data.local.entity.BlacklistEntity
import com.example.scam_warming_app.data.remote.ApiService
import com.example.scam_warming_app.domain.repository.IBlacklistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BlacklistRepositoryImpl @Inject constructor(
    private val blacklistDao: BlacklistDao,
    private val apiService: ApiService
) : IBlacklistRepository {

    override fun getFullBlacklist(): Flow<List<BlacklistEntity>> {
        return blacklistDao.getFullBlacklist()
    }

    override suspend fun syncBlacklist(): Result<Unit> {
        return try {
            val response = apiService.getBlacklist()
            val entities = response.data.map { dto ->
                BlacklistEntity(
                    phoneNumber = dto.phone_number,
                    category = dto.category,
                    riskLevel = dto.risk_level,
                    reportedCount = dto.report_count ?: 1 // Cập nhật số lần báo cáo từ Server
                )
            }
            blacklistDao.clearAll()
            blacklistDao.insertAll(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isNumberBlacklisted(phoneNumber: String): BlacklistEntity? {
        return blacklistDao.checkNumber(phoneNumber)
    }
}
