package com.example.scam_warming_app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.scam_warming_app.data.local.dao.AiLogDao
import com.example.scam_warming_app.data.local.dao.BlacklistDao
import com.example.scam_warming_app.data.local.dao.CallDao
import com.example.scam_warming_app.data.local.dao.SmsDao
import com.example.scam_warming_app.data.local.dao.TrustedNumberDao
import com.example.scam_warming_app.data.local.entity.AiLogEntity
import com.example.scam_warming_app.data.local.entity.BlacklistEntity
import com.example.scam_warming_app.data.local.entity.CallEntity
import com.example.scam_warming_app.data.local.entity.SmsEntity
import com.example.scam_warming_app.data.local.entity.TrustedNumberEntity

@Database(
    entities = [
        SmsEntity::class, 
        CallEntity::class, 
        BlacklistEntity::class, 
        TrustedNumberEntity::class,
        AiLogEntity::class
    ],
    version = 6, // NÂNG CẤP LÊN 6 ĐỂ FIX LỖI CRASH
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun smsDao(): SmsDao
    abstract fun callDao(): CallDao
    abstract fun blacklistDao(): BlacklistDao
    abstract fun trustedNumberDao(): TrustedNumberDao
    abstract fun aiLogDao(): AiLogDao
}
