package com.paparazziapps.pretamistapp.data.repository

import com.paparazziapps.pretamistapp.data.network.NetworkOperation
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.remote.RemoteEmailDataSource
import com.paparazziapps.pretamistapp.data.utils.ReceiptHtmlTemplate
import com.paparazziapps.pretamistapp.domain.notification.EmailReceiptResponse
import com.paparazziapps.pretamistapp.domain.notification.EmailRequest
import com.paparazziapps.pretamistapp.domain.notification.ReceiptParams
import com.paparazziapps.pretamistapp.frameworks.network.NetworkModule

class PAEmailRepositoryImpl(
    private val remoteEmailDataSource: RemoteEmailDataSource,
    private val htmlTemplate: ReceiptHtmlTemplate
): PAEmailRepository {

    override suspend fun sendPaymentReceipt(
        recipientEmail: String,
        amount: Double,
        date: String,
        operationCode: String
    ): PAResult<EmailReceiptResponse> {
        return NetworkOperation.safeApiCallWithResult {
            //handle other operations here this must be in the use case layer
            val params = ReceiptParams(
                recipientName = "Antony",
                amount = amount,
                date = date,
                operationCode = operationCode
            )

            val htmlContent = htmlTemplate.create(params)

            val emailRequest = EmailRequest(
                from = "PrestamistApp <prestamistapp@resend.dev>",
                to = listOf(recipientEmail),
                subject = params.subject,
                html = htmlContent
            )
            remoteEmailDataSource.sendEmail(emailRequest)
        }
    }

}