package com.example.scam_warming_app.ai

import kotlinx.coroutines.flow.Flow

interface SpeechToTextService {
    fun startListening(): Flow<String>
    fun stopListening()
    fun isModelReady(): Boolean
}
