package com.example.scam_warming_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trusted_numbers")
data class TrustedNumberEntity(
    @PrimaryKey val phoneNumber: String,
    val name: String,
    val addedDate: Long = System.currentTimeMillis()
)
