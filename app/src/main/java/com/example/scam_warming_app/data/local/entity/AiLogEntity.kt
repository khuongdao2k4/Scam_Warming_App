package com.example.scam_warming_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_logs")
data class AiLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceType: String, // SMS hoặc CALL
    val processingMode: String, // ONLINE hoặc OFFLINE
    val riskScore: Int,
    val processingTime: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
