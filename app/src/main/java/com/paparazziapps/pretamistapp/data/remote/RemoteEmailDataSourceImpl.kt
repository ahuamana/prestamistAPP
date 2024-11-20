package com.paparazziapps.pretamistapp.data.remote

import com.paparazziapps.pretamistapp.data.network.NetworkOperation
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.services.ServiceProvider
import com.paparazziapps.pretamistapp.domain.notification.EmailReceiptResponse
import com.paparazziapps.pretamistapp.domain.notification.EmailRequest

class RemoteEmailDataSourceImpl(
    private val serviceProvider: ServiceProvider
): RemoteEmailDataSource {
    override suspend fun sendEmail(
        emailRequest: EmailRequest
    ): PAResult<EmailReceiptResponse> {
        return NetworkOperation.safeApiCallV2 {
            serviceProvider.emailService().sendEmail(emailRequest)
        }
    }
}