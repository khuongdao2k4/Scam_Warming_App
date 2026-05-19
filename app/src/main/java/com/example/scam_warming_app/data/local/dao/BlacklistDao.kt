package com.example.scam_warming_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.scam_warming_app.data.local.entity.BlacklistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlacklistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<BlacklistEntity>)

    @Query("SELECT * FROM blacklist")
    fun getFullBlacklist(): Flow<List<BlacklistEntity>>

    @Query("SELECT * FROM blacklist WHERE phoneNumber = :phone LIMIT 1")
    suspend fun checkNumber(phone: String): BlacklistEntity?

    @Query("SELECT COUNT(*) FROM blacklist")
    suspend fun getCount(): Int

    @Query("DELETE FROM blacklist")
    suspend fun clearAll()
}
