package com.example.scam_warming_app.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.scam_warming_app.data.local.dao.AiLogDao
import com.example.scam_warming_app.data.remote.ApiService
import com.example.scam_warming_app.data.remote.AiLogRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class AiLogSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val aiLogDao: AiLogDao,
    private val apiService: ApiService
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val logs = aiLogDao.getUnsyncedLogs().first()
            if (logs.isEmpty()) return Result.success()

            var allSuccess = true
            logs.forEach { log ->
                try {
                    val request = AiLogRequest(
                        source_type = log.sourceType,
                        processing_mode = log.processingMode,
                        risk_score = log.riskScore,
                        processing_time = log.processingTime
                    )
                    // Sử dụng logAiResult từ ApiService
                    apiService.logAiResult(request)
                } catch (e: Exception) {
                    allSuccess = false
                }
            }

            if (allSuccess) {
                aiLogDao.markAsSynced(logs.map { it.id })
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
