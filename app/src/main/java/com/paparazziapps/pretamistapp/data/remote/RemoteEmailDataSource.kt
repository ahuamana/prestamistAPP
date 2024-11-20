package com.paparazziapps.pretamistapp.data.remote

import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.notification.EmailReceiptResponse
import com.paparazziapps.pretamistapp.domain.notification.EmailRequest

interface RemoteEmailDataSource {
    suspend fun sendEmail(
        emailRequest: EmailRequest
    ): PAResult<EmailReceiptResponse>
}