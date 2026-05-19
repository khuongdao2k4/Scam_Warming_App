package com.example.scam_warming_app.data.repository

import com.example.scam_warming_app.data.remote.ApiService
import com.example.scam_warming_app.data.remote.ReportRequest
import com.example.scam_warming_app.domain.model.ReportData
import com.example.scam_warming_app.domain.repository.IReportRepository
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : IReportRepository {
    override suspend fun submitReport(report: ReportData): Result<Unit> {
        return try {
            val response = apiService.submitReport(
                ReportRequest(
                    phone_number = report.phoneNumber,
                    report_type = report.reportType,
                    description = report.description
                )
            )
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
