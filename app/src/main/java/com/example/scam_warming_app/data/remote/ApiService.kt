package com.example.scam_warming_app.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    // Phân tích
    @POST("analyze/sms")
    suspend fun analyzeSms(@Body request: SmsAnalysisRequest): AnalysisResponse

    @POST("analyze/call")
    suspend fun analyzeCall(@Body request: CallAnalysisRequest): AnalysisResponse

    // Dữ liệu
    @GET("blacklist")
    suspend fun getBlacklist(): BlacklistResponse

    @GET("scam-keywords")
    suspend fun getKeywords(): KeywordsResponse

    @POST("report")
    suspend fun submitReport(@Body request: ReportRequest): ReportResponse

    // Xác thực (Theo Section 14 của API Spec)
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshRequest): AuthResponse

    // AI Logging (Theo Section 7.1 của API Spec)
    @POST("ai/log")
    suspend fun logAiResult(@Body request: AiLogRequest): Unit
}

data class SmsAnalysisRequest(
    val phone_number: String,
    val message: String,
    val device_id: String
)

data class CallAnalysisRequest(
    val phone_number: String,
    val transcript: String,
    val call_time: String
)

data class AnalysisResponse(
    val risk_score: Int,
    val is_scam: Boolean,
    val category: String?,
    val reasons: List<String>?,
    val warning_message: String?
)

data class RegisterRequest(
    val phone_number: String,
    val device_id: String,
    val device_model: String,
    val os_version: String
)

data class LoginRequest(
    val phone_number: String,
    val device_id: String
)

data class RefreshRequest(
    val refresh_token: String
)

data class AuthResponse(
    val success: Boolean,
    val access_token: String?,
    val refresh_token: String?,
    val expires_in: Int?
)

data class ReportRequest(
    val phone_number: String,
    val report_type: String,
    val description: String
)

data class ReportResponse(
    val success: Boolean,
    val message: String
)

data class AiLogRequest(
    val source_type: String,
    val processing_mode: String,
    val risk_score: Int,
    val processing_time: Double
)

data class BlacklistResponse(
    val data: List<BlacklistDto>
)

data class BlacklistDto(
    val phone_number: String,
    val category: String,
    val risk_level: Int
)

data class KeywordsResponse(
    val keywords: List<String>
)
