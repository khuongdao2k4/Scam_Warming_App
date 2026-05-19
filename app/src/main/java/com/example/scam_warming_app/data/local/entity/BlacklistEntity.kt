package com.example.scam_warming_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blacklist")
data class BlacklistEntity(
    @PrimaryKey val phoneNumber: String,
    val category: String, // Loại lừa đảo: Tài chính, Giả danh...
    val riskLevel: Int,   // Mức độ nguy hiểm 0-100
    val reportedCount: Int = 1,
    val lastUpdated: Long = System.currentTimeMillis()
)
