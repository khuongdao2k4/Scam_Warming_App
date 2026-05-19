package com.example.scam_warming_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.scam_warming_app.data.local.entity.AiLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AiLogDao {
    @Insert
    suspend fun insertLog(log: AiLogEntity)

    @Query("SELECT * FROM ai_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AiLogEntity>>

    @Query("SELECT * FROM ai_logs WHERE sourceType = :type ORDER BY timestamp DESC")
    fun getLogsByType(type: String): Flow<List<AiLogEntity>>

    @Query("SELECT * FROM ai_logs WHERE isSynced = 0")
    fun getUnsyncedLogs(): Flow<List<AiLogEntity>>

    @Query("UPDATE ai_logs SET isSynced = 1 WHERE id IN (:logIds)")
    suspend fun markAsSynced(logIds: List<Long>)

    @Query("DELETE FROM ai_logs")
    suspend fun clearLogs()
}
