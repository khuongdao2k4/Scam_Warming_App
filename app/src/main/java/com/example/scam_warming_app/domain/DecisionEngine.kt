package com.example.scam_warming_app.domain

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DecisionEngine @Inject constructor() {

    fun decide(score: Int): DecisionAction {
        return when {
            score >= 85 -> DecisionAction.SCAM_WITH_BLOCK_SUGGESTION
            score >= 80 -> DecisionAction.SCAM
            score >= 61 -> DecisionAction.DANGEROUS
            score >= 31 -> DecisionAction.SUSPICIOUS
            else -> DecisionAction.SAFE
        }
    }
}

enum class DecisionAction {
    SAFE,                           // An toàn
    SUSPICIOUS,                     // Nghi ngờ (Hiện Notification vàng)
    DANGEROUS,                      // Nguy hiểm (Hiện Overlay cam)
    SCAM,                            // Lừa đảo (Hiện Overlay đỏ)
    SCAM_WITH_BLOCK_SUGGESTION      // Lừa đảo cực độ (Hiện Overlay đỏ + nút Chặn)
}
