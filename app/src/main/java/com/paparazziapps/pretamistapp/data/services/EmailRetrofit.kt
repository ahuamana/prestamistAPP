package com.paparazziapps.pretamistapp.data.services

import com.paparazziapps.pretamistapp.domain.notification.EmailRequest
import com.paparazziapps.pretamistapp.domain.notification.EmailReceiptResponse
import com.paparazziapps.pretamistapp.frameworks.network.ResendConstants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface EmailRetrofit {

    @POST(ResendConstants.EMAILS)
    suspend fun sendEmail(
      @Body emailRequest: EmailRequest
    ) :Response<EmailReceiptResponse>

}