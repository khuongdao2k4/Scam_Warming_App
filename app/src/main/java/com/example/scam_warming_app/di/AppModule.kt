package com.example.scam_warming_app.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Cung cấp Context mặc định cho toàn bộ ứng dụng.
     * Đây là "chốt chặn" cuối cùng để sửa lỗi MissingBinding khi các lớp (như GemmaAiEngine)
     * yêu cầu Context mà trình biên dịch Dagger không nhận diện được Qualifier.
     */
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }
}
