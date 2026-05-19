package com.example.scam_warming_app.presentation.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scam_warming_app.domain.repository.IBlacklistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val blacklistRepository: IBlacklistRepository
) : ViewModel() {

    private val prefs: SharedPreferences = context.getSharedPreferences("scam_settings", Context.MODE_PRIVATE)

    private val _isCallProtectionEnabled = MutableStateFlow(prefs.getBoolean("call_protection", true))
    val isCallProtectionEnabled = _isCallProtectionEnabled.asStateFlow()

    private val _isSmsProtectionEnabled = MutableStateFlow(prefs.getBoolean("sms_protection", true))
    val isSmsProtectionEnabled = _isSmsProtectionEnabled.asStateFlow()

    private val _riskThreshold = MutableStateFlow(prefs.getInt("risk_threshold", 60))
    val riskThreshold = _riskThreshold.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    fun toggleCallProtection(enabled: Boolean) {
        _isCallProtectionEnabled.value = enabled
        prefs.edit { putBoolean("call_protection", enabled) }
    }

    fun toggleSmsProtection(enabled: Boolean) {
        _isSmsProtectionEnabled.value = enabled
        prefs.edit { putBoolean("sms_protection", enabled) }
    }

    fun setRiskThreshold(value: Int) {
        _riskThreshold.value = value
        prefs.edit { putInt("risk_threshold", value) }
    }

    fun syncData(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isSyncing.value = true
            val result = blacklistRepository.syncBlacklist()
            _isSyncing.value = false
            onComplete(result.isSuccess)
        }
    }
}
