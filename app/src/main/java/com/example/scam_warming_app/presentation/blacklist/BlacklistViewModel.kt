package com.example.scam_warming_app.presentation.blacklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scam_warming_app.data.local.entity.BlacklistEntity
import com.example.scam_warming_app.domain.repository.IBlacklistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlacklistViewModel @Inject constructor(
    private val blacklistRepository: IBlacklistRepository
) : ViewModel() {

    val blacklist: StateFlow<List<BlacklistEntity>> = blacklistRepository.getFullBlacklist()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun syncBlacklist() {
        viewModelScope.launch {
            blacklistRepository.syncBlacklist()
        }
    }
}
