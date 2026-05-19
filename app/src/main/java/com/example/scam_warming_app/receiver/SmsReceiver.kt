package com.example.scam_warming_app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.example.scam_warming_app.domain.usecase.AnalyzeSmsUseCase
import com.example.scam_warming_app.utils.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SmsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var analyzeSmsUseCase: AnalyzeSmsUseCase

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            val notificationHelper = context?.let { NotificationHelper(it) }

            for (message in messages) {
                val sender = message.displayOriginatingAddress ?: "Unknown"
                val body = message.displayMessageBody ?: ""
                
                Log.d("SmsReceiver", "Processing SMS from $sender")

                scope.launch {
                    try {
                        val result = analyzeSmsUseCase(sender, body)
                        if (result.isScam) {
                            notificationHelper?.showScamWarning(
                                sender = sender,
                                category = result.category ?: "Lừa đảo",
                                score = result.riskScore
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("SmsReceiver", "Error analyzing SMS", e)
                    }
                }
            }
        }
    }
}
