package com.example.scam_warming_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "call_history")
data class CallEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val phoneNumber: String,
    val timestamp: Long,
    val duration: Long = 0,
    val riskScore: Int,
    val isScam: Boolean,
    val category: String?,
    val transcript: String? = null
)
