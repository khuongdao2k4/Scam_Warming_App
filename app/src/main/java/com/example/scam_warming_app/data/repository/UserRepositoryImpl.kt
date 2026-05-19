package com.example.scam_warming_app.data.repository

import android.content.Context
import android.os.Build
import com.example.scam_warming_app.data.local.SessionManager
import com.example.scam_warming_app.data.remote.ApiService
import com.example.scam_warming_app.data.remote.RegisterRequest
import com.example.scam_warming_app.domain.repository.IUserRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager,
    @ApplicationContext private val context: Context
) : IUserRepository {

    override suspend fun registerDevice(phoneNumber: String, deviceId: String): Result<Unit> {
        return try {
            val response = apiService.register(
                RegisterRequest(
                    phone_number = phoneNumber,
                    device_id = deviceId,
                    device_model = "${Build.MANUFACTURER} ${Build.MODEL}",
                    os_version = "Android ${Build.VERSION.RELEASE}"
                )
            )
            if (response.success && response.access_token != null) {
                sessionManager.saveAuthToken(response.access_token)
                response.refresh_token?.let { sessionManager.saveRefreshToken(it) }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Đăng ký thiết bị thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()

    override fun logout() {
        sessionManager.clearSession()
    }
}
