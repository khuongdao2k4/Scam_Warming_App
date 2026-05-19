package com.example.scam_warming_app.domain.usecase

import com.example.scam_warming_app.data.local.dao.TrustedNumberDao
import com.example.scam_warming_app.data.local.entity.TrustedNumberEntity
import javax.inject.Inject

class AddTrustedNumberUseCase @Inject constructor(
    private val trustedNumberDao: TrustedNumberDao
) {
    suspend operator fun invoke(phoneNumber: String, name: String) {
        trustedNumberDao.insertTrustedNumber(
            TrustedNumberEntity(phoneNumber = phoneNumber, name = name)
        )
    }
}
