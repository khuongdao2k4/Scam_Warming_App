package com.example.scam_warming_app.domain.usecase

import com.example.scam_warming_app.domain.repository.ICallRepository
import com.example.scam_warming_app.domain.repository.ISmsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    private val smsRepository: ISmsRepository,
    private val callRepository: ICallRepository
) {
    operator fun invoke(): Flow<List<HistoryItem>> {
        return combine(
            smsRepository.getAllSms(),
            callRepository.getAllCalls()
        ) { smsList, callList ->
            val items = mutableListOf<HistoryItem>()
            
            smsList.forEach { 
                items.add(HistoryItem.Sms(it.id, it.sender, it.message, it.timestamp, it.isScam, it.category))
            }
            
            callList.forEach { 
                items.add(HistoryItem.Call(it.id, it.phoneNumber, it.timestamp, it.isScam, it.category))
            }
            
            items.sortedByDescending { it.timestamp }
        }
    }
}

sealed class HistoryItem(
    val id: Long,
    val identifier: String,
    val timestamp: Long,
    val isScam: Boolean,
    val category: String?
) {
    class Sms(id: Long, sender: String, val message: String, timestamp: Long, isScam: Boolean, category: String?) : 
        HistoryItem(id, sender, timestamp, isScam, category)
        
    class Call(id: Long, phoneNumber: String, timestamp: Long, isScam: Boolean, category: String?) : 
        HistoryItem(id, phoneNumber, timestamp, isScam, category)
}
