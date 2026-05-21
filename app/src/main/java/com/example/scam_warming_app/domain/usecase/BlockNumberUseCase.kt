package com.example.scam_warming_app.domain.usecase

import android.content.ContentValues
import android.content.Context
import android.provider.BlockedNumberContract
import android.util.Log
import com.example.scam_warming_app.domain.model.ReportData
import com.example.scam_warming_app.domain.repository.IReportRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BlockNumberUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val reportRepository: IReportRepository
) {
    suspend operator fun invoke(phoneNumber: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. Thực hiện chặn số ở mức hệ thống Android
            val values = ContentValues().apply {
                put(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER, phoneNumber)
            }
            context.contentResolver.insert(BlockedNumberContract.BlockedNumbers.CONTENT_URI, values)
            Log.d("BlockNumber", "Đã chặn số thành công: $phoneNumber")

            // 2. TỰ ĐỘNG GỬI BÁO CÁO LÊN SERVER
            // Chúng ta gửi báo cáo ngầm để đóng góp vào dữ liệu cộng đồng
            reportRepository.submitReport(
                ReportData(
                    phoneNumber = phoneNumber,
                    reportType = "Auto-Blocked",
                    description = "Người dùng đã chặn số này sau khi nhận cảnh báo từ AI."
                )
            )
            Log.d("BlockNumber", "Đã tự động gửi báo cáo cộng đồng cho số: $phoneNumber")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("BlockNumber", "Lỗi khi chặn số hoặc gửi báo cáo", e)
            Result.failure(e)
        }
    }
}
