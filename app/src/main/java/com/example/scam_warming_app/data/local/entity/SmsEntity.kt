package com.example.scam_warming_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sms_history")
data class SmsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String,
    val message: String,
    val timestamp: Long,
    val riskScore: Int,
    val isScam: Boolean,
    val category: String?,
    val reasons: String // Store as JSON string or comma-separated
)
