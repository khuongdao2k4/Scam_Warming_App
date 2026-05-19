package com.example.scam_warming_app.presentation.trusted

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scam_warming_app.data.local.dao.TrustedNumberDao
import com.example.scam_warming_app.data.local.entity.TrustedNumberEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrustedNumbersViewModel @Inject constructor(
    private val trustedNumberDao: TrustedNumberDao
) : ViewModel() {

    val trustedNumbers: StateFlow<List<TrustedNumberEntity>> = trustedNumberDao.getAllTrustedNumbers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addTrustedNumber(phone: String, name: String) {
        viewModelScope.launch {
            trustedNumberDao.insertTrustedNumber(TrustedNumberEntity(phone, name))
        }
    }

    fun deleteTrustedNumber(number: TrustedNumberEntity) {
        viewModelScope.launch {
            trustedNumberDao.deleteTrustedNumber(number)
        }
    }
}
