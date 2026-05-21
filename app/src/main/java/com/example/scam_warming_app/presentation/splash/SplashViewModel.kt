package com.example.scam_warming_app.presentation.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scam_warming_app.data.local.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    private val _nextDestination = MutableStateFlow("onboarding")
    val nextDestination = _nextDestination.asStateFlow()

    init {
        checkStatus()
    }

    private fun checkStatus() {
        viewModelScope.launch {
            val prefs = context.getSharedPreferences("scam_app_prefs", Context.MODE_PRIVATE)
            val onboardingDone = prefs.getBoolean("onboarding_done", false)
            
            // Xác định điểm đến tiếp theo
            _nextDestination.value = when {
                sessionManager.isLoggedIn() -> "main"
                onboardingDone -> "permission"
                else -> "onboarding"
            }

            // Đợi 2 giây để hiển thị thương hiệu
            delay(2000)
            _isReady.value = true
        }
    }
}
