package com.example.scam_warming_app.domain.repository

interface IUserRepository {
    suspend fun registerDevice(phoneNumber: String, deviceId: String): Result<Unit>
    fun isLoggedIn(): Boolean
    fun logout()
}
