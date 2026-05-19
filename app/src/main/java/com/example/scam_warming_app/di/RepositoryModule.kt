package com.example.scam_warming_app.di

import com.example.scam_warming_app.data.repository.BlacklistRepositoryImpl
import com.example.scam_warming_app.data.repository.CallRepositoryImpl
import com.example.scam_warming_app.data.repository.ReportRepositoryImpl
import com.example.scam_warming_app.data.repository.SmsRepositoryImpl
import com.example.scam_warming_app.data.repository.UserRepositoryImpl
import com.example.scam_warming_app.domain.repository.IBlacklistRepository
import com.example.scam_warming_app.domain.repository.ICallRepository
import com.example.scam_warming_app.domain.repository.IReportRepository
import com.example.scam_warming_app.domain.repository.ISmsRepository
import com.example.scam_warming_app.domain.repository.IUserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSmsRepository(
        smsRepositoryImpl: SmsRepositoryImpl
    ): ISmsRepository

    @Binds
    @Singleton
    abstract fun bindCallRepository(
        callRepositoryImpl: CallRepositoryImpl
    ): ICallRepository

    @Binds
    @Singleton
    abstract fun bindBlacklistRepository(
        blacklistRepositoryImpl: BlacklistRepositoryImpl
    ): IBlacklistRepository

    @Binds
    @Singleton
    abstract fun bindReportRepository(
        reportRepositoryImpl: ReportRepositoryImpl
    ): IReportRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): IUserRepository
}
