package com.paparazziapps.pretamistapp.domain.notification

import androidx.annotation.Keep
import javax.security.auth.Subject

@Keep
data class EmailRequest(
    val from: String,
    val to: List<String>,
    val subject: String,
    val html: String
)

//createCustomResend using
