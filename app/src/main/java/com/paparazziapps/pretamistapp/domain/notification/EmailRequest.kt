package com.paparazziapps.pretamistapp.domain.notification

import javax.security.auth.Subject

data class EmailRequest(
    val from: String,
    val to: List<String>,
    val subject: String,
    val html: String
)

//createCustomResend using
