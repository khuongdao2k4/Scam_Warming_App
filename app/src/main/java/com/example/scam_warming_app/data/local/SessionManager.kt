package com.example.scam_warming_app.data.local

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private var prefs = try {
        createSharedPrefs()
    } catch (e: Exception) {
        Log.e("SessionManager", "Lỗi khởi tạo Prefs mã hóa, đang thử xóa và tạo lại", e)
        context.getSharedPreferences("secure_session_prefs", Context.MODE_PRIVATE).edit().clear().apply()
        createSharedPrefs()
    }

    private fun createSharedPrefs() = EncryptedSharedPreferences.create(
        context,
        "secure_session_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveAuthToken(token: String) {
        prefs.edit().putString("access_token", token).apply()
    }

    fun getAuthToken(): String? = prefs.getString("access_token", null)

    fun saveRefreshToken(token: String) {
        prefs.edit().putString("refresh_token", token).apply()
    }

    fun getRefreshToken(): String? = prefs.getString("refresh_token", null)

    fun isLoggedIn(): Boolean = !getAuthToken().isNullOrEmpty()

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
