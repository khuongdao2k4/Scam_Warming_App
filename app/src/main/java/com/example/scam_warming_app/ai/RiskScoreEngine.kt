package com.example.scam_warming_app.ai

import com.example.scam_warming_app.data.local.dao.TrustedNumberDao
import com.example.scam_warming_app.domain.repository.IBlacklistRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RiskScoreEngine @Inject constructor(
    private val blacklistRepository: IBlacklistRepository,
    private val trustedNumberDao: TrustedNumberDao,
    private val urlAnalyzer: UrlAnalyzer
) {

    private val keywordScores = mapOf(
        "công an" to 35,
        "viện kiểm sát" to 35,
        "lệnh bắt" to 40,
        "otp" to 30,
        "đầu tư lợi nhuận" to 30,
        "chuyển khoản" to 25,
        "tài khoản bị khóa" to 25,
        "xác minh tài khoản" to 20,
        "đăng nhập ngay" to 20,
        "trúng thưởng" to 15,
        "nhận quà" to 15
    )

    private val manipulationPatterns = mapOf(
        "đe dọa" to listOf("bị bắt", "khởi tố", "pháp luật", "truy tố", "cưỡng chế"),
        "thúc ép" to listOf("ngay lập tức", "khẩn cấp", "trong 5 phút", "ngay bây giờ"),
        "bí mật" to listOf("không được nói", "giữ kín", "đừng báo ai")
    )

    suspend fun calculateRiskScore(
        phoneNumber: String, 
        content: String,
        isSpoofed: Boolean = false
    ): RiskResult {
        // 0. KIỂM TRA SỐ TIN CẬY
        if (trustedNumberDao.isTrusted(phoneNumber)) {
            return RiskResult(0, false, "Số điện thoại tin cậy", emptyList())
        }

        // 1. KIỂM TRA GIẢ MẠO DANH TÍNH (Ưu tiên cao nhất)
        if (isSpoofed) {
            return RiskResult(100, true, "Phát hiện giả mạo danh tính người thân", listOf("Số điện thoại thực tế không khớp với tên hiển thị"))
        }

        val lowerContent = content.lowercase()
        var totalScore = 0
        val matchedKeywords = mutableListOf<String>()

        // 2. KIỂM TRA BLACKLIST
        val blacklistEntry = blacklistRepository.isNumberBlacklisted(phoneNumber)
        if (blacklistEntry != null) {
            totalScore += 60
            matchedKeywords.add("Số điện thoại nằm trong danh sách đen")
        }

        // 3. PHÂN TÍCH TỪ KHÓA
        keywordScores.forEach { (keyword, score) ->
            if (lowerContent.contains(keyword)) {
                totalScore += score
                matchedKeywords.add(keyword)
            }
        }

        // 4. PHÂN TÍCH THAO TÚNG TÂM LÝ
        manipulationPatterns.forEach { (type, patterns) ->
            if (patterns.any { lowerContent.contains(it) }) {
                totalScore += 25
                matchedKeywords.add("Dấu hiệu $type")
            }
        }

        // 5. PHÂN TÍCH URL
        val urlResult = urlAnalyzer.analyzeUrls(content)
        if (urlResult.riskScore > 0) {
            totalScore += urlResult.riskScore
            matchedKeywords.addAll(urlResult.reasons)
        }

        // 6. LOGIC KẾT HỢP NGUY HIỂM
        if (lowerContent.contains("công an") && (lowerContent.contains("chuyển khoản") || lowerContent.contains("tiền"))) {
            totalScore += 50
        }

        val finalScore = totalScore.coerceAtMost(100)

        return RiskResult(
            score = finalScore,
            isScam = finalScore >= 60,
            category = blacklistEntry?.category ?: determineCategory(matchedKeywords, lowerContent),
            matchedKeywords = matchedKeywords
        )
    }

    private fun determineCategory(keywords: List<String>, content: String): String {
        return when {
            content.contains("công an") || content.contains("lệnh bắt") -> "Giả danh cơ quan chức năng"
            content.contains("otp") || content.contains("ngân hàng") -> "Chiếm đoạt tài khoản"
            content.contains("đầu tư") || content.contains("lợi nhuận") -> "Lừa đảo tài chính"
            keywords.any { it.contains("Sử dụng dịch vụ rút gọn link") } -> "Tin nhắn chứa link độc hại"
            else -> "Nghi ngờ lừa đảo"
        }
    }
}

data class RiskResult(
    val score: Int,
    val isScam: Boolean,
    val category: String,
    val matchedKeywords: List<String>
)
