package com.example.scam_warming_app.ai

import android.util.Patterns
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UrlAnalyzer @Inject constructor() {

    // Danh sách các domain rút gọn thường dùng trong lừa đảo (AI Rules 5.2)
    private val suspiciousShorteners = listOf(
        "bit.ly", "tinyurl.com", "goo.gl", "shorturl.at", "t.co", "cutt.ly"
    )

    // Từ khóa giả mạo ngân hàng/tổ chức (AI Rules 5.3)
    private val bankKeywords = listOf(
        "vcb", "vietcombank", "mbbank", "bidv", "shopee", "tiki", "lazada", "ebank", "security"
    )

    /**
     * Phân tích mức độ rủi ro của các URL trong văn bản
     */
    fun analyzeUrls(text: String): UrlAnalysisResult {
        val urls = extractUrls(text)
        if (urls.isEmpty()) return UrlAnalysisResult(0, emptyList())

        var totalRisk = 0
        val reasons = mutableListOf<String>()

        urls.forEach { url ->
            val lowerUrl = url.lowercase()

            // 1. Kiểm tra link rút gọn
            if (suspiciousShorteners.any { lowerUrl.contains(it) }) {
                totalRisk += 30
                reasons.add("Sử dụng dịch vụ rút gọn link ($url) để che giấu đích đến")
            }

            // 2. Kiểm tra link không có HTTPS (AI Rules 5.1)
            if (lowerUrl.startsWith("http://")) {
                totalRisk += 20
                reasons.add("Liên kết không an toàn (thiếu bảo mật HTTPS)")
            }

            // 3. Kiểm tra địa chỉ IP (AI Rules 5.1)
            if (url.matches(Regex(""".*(\d{1,3}\.){3}\d{1,3}.*"""))) {
                totalRisk += 40
                reasons.add("Liên kết sử dụng địa chỉ IP trực tiếp, dấu hiệu trang web lừa đảo")
            }

            // 4. Kiểm tra giả mạo domain (Fake Domain Pattern)
            if (isFakeBankDomain(lowerUrl)) {
                totalRisk += 50
                reasons.add("Tên miền có dấu hiệu giả mạo ngân hàng/thương mại điện tử")
            }
        }

        return UrlAnalysisResult(
            riskScore = totalRisk.coerceAtMost(100),
            reasons = reasons
        )
    }

    private fun extractUrls(text: String): List<String> {
        val urls = mutableListOf<String>()
        val matcher = Patterns.WEB_URL.matcher(text)
        while (matcher.find()) {
            urls.add(matcher.group())
        }
        return urls
    }

    private fun isFakeBankDomain(url: String): Boolean {
        // Nếu chứa từ khóa ngân hàng nhưng không phải domain chính thức của họ
        val isBankRelated = bankKeywords.any { url.contains(it) }
        val isOfficial = url.contains(".com.vn") || url.contains(".vn")
        
        // Pattern: vietcombank-security.net, shopee-qua-tang.info...
        return isBankRelated && !isOfficial
    }
}

data class UrlAnalysisResult(
    val riskScore: Int,
    val reasons: List<String>
)
