package com.paparazziapps.pretamistapp.domain.notification

// Data class to encapsulate template parameters for a receipt email
data class ReceiptParams(
    val recipientName: String,
    val amount: Double,
    val date: String,
    val operationCode: String,
    val subject: String = "Boucher de Préstamo",
    val logoUrl: String = "https://github.com/user-attachments/assets/4104034f-55c7-4529-b806-cca68264baea"
)
