package com.example.scam_warming_app.domain.model

data class AnalysisResult(
    val riskScore: Int,
    val isScam: Boolean,
    val category: String?,
    val reasons: List<String>,
    val recommendation: String?,
    val isOfflineMode: Boolean = false
)

enum class RiskLevel {
    SAFE, SUSPICIOUS, DANGEROUS, SCAM
}
