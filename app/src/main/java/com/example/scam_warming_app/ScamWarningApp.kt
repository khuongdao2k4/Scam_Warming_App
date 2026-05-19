package com.example.scam_warming_app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.example.scam_warming_app.service.AiLogSyncWorker
import com.example.scam_warming_app.service.SyncBlacklistWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class ScamWarningApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        setupBackgroundWorkers()
    }

    private fun setupBackgroundWorkers() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        // 1. Tự động cập nhật danh sách đen mỗi 24h
        val syncRequest = PeriodicWorkRequestBuilder<SyncBlacklistWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "sync_blacklist_work",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )

        // 2. Tự động đồng bộ nhật ký AI mỗi 12h
        val aiLogRequest = PeriodicWorkRequestBuilder<AiLogSyncWorker>(12, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "sync_ai_logs_work",
            ExistingPeriodicWorkPolicy.KEEP,
            aiLogRequest
        )
    }
}
