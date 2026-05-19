package com.example.scam_warming_app.di

import android.content.Context
import androidx.room.Room
import com.example.scam_warming_app.data.local.AppDatabase
import com.example.scam_warming_app.data.local.dao.AiLogDao
import com.example.scam_warming_app.data.local.dao.BlacklistDao
import com.example.scam_warming_app.data.local.dao.CallDao
import com.example.scam_warming_app.data.local.dao.SmsDao
import com.example.scam_warming_app.data.local.dao.TrustedNumberDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        // Khởi tạo thư viện SQLCipher để mã hóa dữ liệu
        SQLiteDatabase.loadLibs(context)
        
        // Khóa mã hóa bảo mật (Passphrase)
        val passphrase = SQLiteDatabase.getBytes("scam-warning-secure-key-2026-v1".toCharArray())
        val factory = SupportFactory(passphrase)

        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "scam_warning_secure_v2.db"
        )
        .openHelperFactory(factory) // Kích hoạt mã hóa tệp tin DB
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideSmsDao(database: AppDatabase): SmsDao = database.smsDao()

    @Provides
    fun provideCallDao(database: AppDatabase): CallDao = database.callDao()

    @Provides
    fun provideBlacklistDao(database: AppDatabase): BlacklistDao = database.blacklistDao()

    @Provides
    fun provideTrustedNumberDao(database: AppDatabase): TrustedNumberDao = database.trustedNumberDao()

    @Provides
    fun provideAiLogDao(database: AppDatabase): AiLogDao = database.aiLogDao()
}
