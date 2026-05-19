package com.example.scam_warming_app.presentation.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scam_warming_app.domain.model.ReportData
import com.example.scam_warming_app.domain.repository.IReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: IReportRepository
) : ViewModel() {

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting = _isSubmitting.asStateFlow()

    fun submitReport(phone: String, type: String, description: String, onSuccess: () -> Unit) {
        if (phone.isBlank()) return
        
        viewModelScope.launch {
            _isSubmitting.value = true
            val report = ReportData(
                phoneNumber = phone,
                reportType = type,
                description = description
            )
            val result = reportRepository.submitReport(report)
            _isSubmitting.value = false
            if (result.isSuccess) {
                onSuccess()
            }
        }
    }
}
