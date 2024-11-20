package com.paparazziapps.pretamistapp.data.repository

import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.notification.EmailReceiptResponse

interface PAEmailRepository {
    suspend fun sendPaymentReceipt(
        recipientEmail: String,
        amount: Double,
        date: String,
        operationCode: String,
        recipientName: String
    ): PAResult<EmailReceiptResponse>
}