package com.example.scam_warming_app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scam_warming_app.ai.GemmaAiEngine
import com.example.scam_warming_app.data.local.dao.BlacklistDao
import com.example.scam_warming_app.data.local.entity.CallEntity
import com.example.scam_warming_app.data.local.entity.SmsEntity
import com.example.scam_warming_app.domain.repository.IBlacklistRepository
import com.example.scam_warming_app.domain.usecase.AnalyzeSmsUseCase
import com.example.scam_warming_app.domain.usecase.DeleteHistoryUseCase
import com.example.scam_warming_app.domain.usecase.GetCallHistoryUseCase
import com.example.scam_warming_app.domain.usecase.GetSmsHistoryUseCase
import com.example.scam_warming_app.utils.DownloadStatus
import com.example.scam_warming_app.utils.ModelDownloadManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSmsHistoryUseCase: GetSmsHistoryUseCase,
    private val getCallHistoryUseCase: GetCallHistoryUseCase,
    private val analyzeSmsUseCase: AnalyzeSmsUseCase, // Thêm UseCase để test
    private val deleteHistoryUseCase: DeleteHistoryUseCase,
    private val blacklistDao: BlacklistDao,
    private val blacklistRepository: IBlacklistRepository,
    private val gemmaAiEngine: GemmaAiEngine,
    private val downloadManager: ModelDownloadManager
) : ViewModel() {

    val smsHistory: StateFlow<List<SmsEntity>> = getSmsHistoryUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val callHistory: StateFlow<List<CallEntity>> = getCallHistoryUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val protectionStats = combine(smsHistory, callHistory) { sms, calls ->
        ProtectionStats(blockedSms = sms.count { it.isScam }, analyzedCalls = calls.size)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProtectionStats())

    private val _blacklistCount = MutableStateFlow(0)
    val blacklistCount = _blacklistCount.asStateFlow()

    private val _isAiReady = MutableStateFlow(gemmaAiEngine.isModelLoaded())
    val isAiReady = _isAiReady.asStateFlow()

    private val _downloadStatus = MutableStateFlow<DownloadStatus>(DownloadStatus.Idle)
    val downloadStatus = _downloadStatus.asStateFlow()

    init {
        updateBlacklistCount()
        autoStartDownloadIfMissing()
        syncDataFromServer()
    }

    private fun syncDataFromServer() {
        viewModelScope.launch {
            blacklistRepository.syncBlacklist()
            updateBlacklistCount()
        }
    }

    private fun autoStartDownloadIfMissing() {
        if (!gemmaAiEngine.isModelLoaded() && _downloadStatus.value is DownloadStatus.Idle) {
            startModelDownload()
        }
    }

    fun startModelDownload() {
        viewModelScope.launch {
            downloadManager.downloadModel().collect { status ->
                _downloadStatus.value = status
                if (status is DownloadStatus.Success) {
                    _isAiReady.value = true
                }
            }
        }
    }

    fun simulateScamSms() {
        viewModelScope.launch {
            // Giả lập một tin nhắn chứa từ khóa "nợ" để test FastAPI
            analyzeSmsUseCase("0912345678", "Bạn đang có một khoản nợ ngân hàng chưa thanh toán!")
        }
    }

    fun installManualModel(inputStream: InputStream) {
        viewModelScope.launch {
            _downloadStatus.value = DownloadStatus.Downloading(0)
            val success = gemmaAiEngine.installModelFromStream(inputStream)
            if (success) {
                _downloadStatus.value = DownloadStatus.Success
                _isAiReady.value = true
            } else {
                _downloadStatus.value = DownloadStatus.Error("Lỗi khi nạp file thủ công")
            }
        }
    }

    fun updateBlacklistCount() {
        viewModelScope.launch {
            _blacklistCount.value = blacklistDao.getCount()
        }
    }

    fun deleteHistory() {
        viewModelScope.launch {
            deleteHistoryUseCase()
        }
    }
}

data class ProtectionStats(
    val blockedSms: Int = 0,
    val analyzedCalls: Int = 0
)
