package com.example.scam_warming_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.scam_warming_app.data.local.entity.SmsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SmsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSms(sms: SmsEntity)

    @Query("SELECT * FROM sms_history ORDER BY timestamp DESC")
    fun getAllSms(): Flow<List<SmsEntity>>

    @Query("SELECT * FROM sms_history WHERE id = :id LIMIT 1")
    suspend fun getSmsById(id: Long): SmsEntity?

    @Query("DELETE FROM sms_history")
    suspend fun deleteAll()
}
