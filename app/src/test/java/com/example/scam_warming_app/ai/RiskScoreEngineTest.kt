package com.example.scam_warming_app.ai

import com.example.scam_warming_app.data.local.entity.BlacklistEntity
import com.example.scam_warming_app.domain.repository.IBlacklistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RiskScoreEngineTest {

    private lateinit var riskScoreEngine: RiskScoreEngine
    private lateinit var fakeBlacklistRepository: FakeBlacklistRepository

    @Before
    fun setup() {
        fakeBlacklistRepository = FakeBlacklistRepository()
        riskScoreEngine = RiskScoreEngine(fakeBlacklistRepository)
    }

    @Test
    fun `Normal message should return SAFE score`() = runBlocking {
        val result = riskScoreEngine.calculateRiskScore("0912345678", "Chào bạn, hôm nay thế nào?")
        assertTrue(result.score < 30)
        assertEquals(false, result.isScam)
    }

    @Test
    fun `Message with one keyword should return moderate score`() = runBlocking {
        val result = riskScoreEngine.calculateRiskScore("0912345678", "Bạn cần cập nhật OTP ngay")
        assertEquals(30, result.score)
        assertTrue(result.matchedKeywords.contains("otp"))
    }

    @Test
    fun `Message with multiple keywords should be marked as SCAM`() = runBlocking {
        val result = riskScoreEngine.calculateRiskScore("0912345678", "Công an thông báo bạn có lệnh bắt, hãy cung cấp otp")
        // công an (35) + lệnh bắt (40) + otp (30) = 105 -> Clamped to 100
        assertEquals(100, result.score)
        assertEquals(true, result.isScam)
    }

    @Test
    fun `Combination of Cong An and Chuyen Khoan should trigger high bonus`() = runBlocking {
        val result = riskScoreEngine.calculateRiskScore("0912345678", "Tôi từ công an, yêu cầu bạn chuyển khoản ngay")
        // công an (35) + chuyển khoản (25) + bonus (50) = 110 -> Clamped to 100
        assertEquals(100, result.score)
        assertEquals("Giả danh cơ quan chức năng", result.category)
    }

    @Test
    fun `Blacklisted phone number should trigger high risk immediately`() = runBlocking {
        val blacklistedNumber = "0123456789"
        fakeBlacklistRepository.addNumber(blacklistedNumber, "Lừa đảo tài chính")
        
        val result = riskScoreEngine.calculateRiskScore(blacklistedNumber, "Xin chào")
        // Blacklist (60) + No keywords (0) = 60
        assertTrue(result.score >= 60)
        assertEquals(true, result.isScam)
        assertEquals("Lừa đảo tài chính", result.category)
    }

    @Test
    fun `Shortened URL should increase risk score`() = runBlocking {
        val result = riskScoreEngine.calculateRiskScore("0912345678", "Bấm vào link này bit.ly/lừa-đảo")
        // URL bonus (25)
        assertEquals(25, result.score)
    }

    // Lớp giả lập Repository để phục vụ kiểm thử
    class FakeBlacklistRepository : IBlacklistRepository {
        private val blacklist = mutableMapOf<String, BlacklistEntity>()

        fun addNumber(phone: String, category: String) {
            blacklist[phone] = BlacklistEntity(phone, category, 100)
        }

        override fun getFullBlacklist(): Flow<List<BlacklistEntity>> = flow {
            emit(blacklist.values.toList())
        }

        override suspend fun syncBlacklist(): Result<Unit> = Result.success(Unit)

        override suspend fun isNumberBlacklisted(phoneNumber: String): BlacklistEntity? {
            return blacklist[phoneNumber]
        }
    }
}
