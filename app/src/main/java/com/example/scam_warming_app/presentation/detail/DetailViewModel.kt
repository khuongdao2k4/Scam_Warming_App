package com.example.scam_warming_app.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scam_warming_app.data.local.entity.CallEntity
import com.example.scam_warming_app.data.local.entity.SmsEntity
import com.example.scam_warming_app.domain.usecase.BlockNumberUseCase
import com.example.scam_warming_app.domain.usecase.GetCallDetailUseCase
import com.example.scam_warming_app.domain.usecase.GetSmsDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getSmsDetailUseCase: GetSmsDetailUseCase,
    private val getCallDetailUseCase: GetCallDetailUseCase,
    private val blockNumberUseCase: BlockNumberUseCase
) : ViewModel() {

    private val _smsDetail = MutableStateFlow<SmsEntity?>(null)
    val smsDetail = _smsDetail.asStateFlow()

    private val _callDetail = MutableStateFlow<CallEntity?>(null)
    val callDetail = _callDetail.asStateFlow()

    private val _isBlocking = MutableStateFlow(false)
    val isBlocking = _isBlocking.asStateFlow()

    fun loadSmsDetail(id: Long) {
        viewModelScope.launch {
            _smsDetail.value = getSmsDetailUseCase(id)
        }
    }

    fun loadCallDetail(id: Long) {
        viewModelScope.launch {
            _callDetail.value = getCallDetailUseCase(id)
        }
    }

    fun blockNumber(phone: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isBlocking.value = true
            val result = blockNumberUseCase(phone)
            _isBlocking.value = false
            onResult(result.isSuccess)
        }
    }
}
