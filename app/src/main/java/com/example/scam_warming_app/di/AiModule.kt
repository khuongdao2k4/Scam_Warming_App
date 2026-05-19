package com.example.scam_warming_app.di

import android.content.Context
import com.example.scam_warming_app.ai.GemmaAiEngine
import com.example.scam_warming_app.ai.GoogleSpeechToTextService
import com.example.scam_warming_app.ai.HybridSpeechToTextService
import com.example.scam_warming_app.ai.SpeechToTextService
import com.example.scam_warming_app.ai.VoskSpeechToTextService
import com.example.scam_warming_app.utils.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {

    @Provides
    @Singleton
    fun provideGemmaAiEngine(@ApplicationContext context: Context): GemmaAiEngine {
        return GemmaAiEngine(context)
    }

    @Provides
    @Singleton
    fun provideSpeechToTextService(
        voskService: VoskSpeechToTextService,
        googleService: GoogleSpeechToTextService,
        networkMonitor: NetworkMonitor
    ): SpeechToTextService {
        return HybridSpeechToTextService(voskService, googleService, networkMonitor)
    }
}
