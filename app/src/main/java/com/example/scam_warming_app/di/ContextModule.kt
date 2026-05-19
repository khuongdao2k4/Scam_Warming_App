package com.example.scam_warming_app.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ContextModule {
    // Để trống để tránh xung đột với AppModule và cơ chế mặc định của Hilt
}
