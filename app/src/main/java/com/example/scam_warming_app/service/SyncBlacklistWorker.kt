package com.example.scam_warming_app.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.scam_warming_app.domain.repository.IBlacklistRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncBlacklistWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val blacklistRepository: IBlacklistRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val result = blacklistRepository.syncBlacklist()
        return if (result.isSuccess) {
            Result.success()
        } else {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}
