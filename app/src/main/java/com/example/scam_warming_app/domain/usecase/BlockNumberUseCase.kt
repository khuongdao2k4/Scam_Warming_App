package com.example.scam_warming_app.domain.usecase

import android.content.ContentValues
import android.content.Context
import android.provider.BlockedNumberContract
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BlockNumberUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(phoneNumber: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val values = ContentValues().apply {
                put(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER, phoneNumber)
            }
            context.contentResolver.insert(BlockedNumberContract.BlockedNumbers.CONTENT_URI, values)
            Log.d("BlockNumber", "Successfully blocked number: $phoneNumber")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("BlockNumber", "Failed to block number", e)
            Result.failure(e)
        }
    }
}
