package com.example.scam_warming_app.data.local.dao

import androidx.room.*
import com.example.scam_warming_app.data.local.entity.TrustedNumberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrustedNumberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrustedNumber(number: TrustedNumberEntity)

    @Delete
    suspend fun deleteTrustedNumber(number: TrustedNumberEntity)

    @Query("SELECT * FROM trusted_numbers ORDER BY name ASC")
    fun getAllTrustedNumbers(): Flow<List<TrustedNumberEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM trusted_numbers WHERE phoneNumber = :phone LIMIT 1)")
    suspend fun isTrusted(phone: String): Boolean
}
