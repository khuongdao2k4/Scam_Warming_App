package com.example.scam_warming_app.domain.repository

import com.example.scam_warming_app.domain.model.ReportData

interface IReportRepository {
    suspend fun submitReport(report: ReportData): Result<Unit>
}
