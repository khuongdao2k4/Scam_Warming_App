package com.example.scam_warming_app.domain.model

data class ReportData(
    val phoneNumber: String,
    val reportType: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)
