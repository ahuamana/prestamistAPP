package com.paparazziapps.pretamistapp.data.utils

import com.paparazziapps.pretamistapp.data.PADataConstants
import com.paparazziapps.pretamistapp.domain.notification.ReceiptParams

class ReceiptHtmlTemplate {
    fun create(params: ReceiptParams): String {
        return """
        <!DOCTYPE html>
        <html>
           <head>
              <meta charset='UTF-8'>
              <title>${params.subject}</title>
           </head>
           <body style='${bodyStyles()}'>
              <div style='${mainContainerStyles()}'>
                 ${logoSection(params.logoUrl)}
                 ${headerSection(params.recipientName)}
                 ${paymentDetailsSection(params)}
              </div>
           </body>
        </html>
        """.trimIndent()
    }

    private fun bodyStyles(): String =
        "font-family: Arial, sans-serif; " +
                "background-color: #f0f2f5; " +
                "margin: 0; " +
                "padding: 0; " +
                "display: flex; " +
                "justify-content: center; " +
                "align-items: center; " +
                "height: 100vh;"

    private fun mainContainerStyles(): String =
        "background-color: #fff; " +
                "padding: 24px; " +
                "border-radius: 16px; " +
                "box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); " +
                "max-width: 400px; " +
                "width: 100%; " +
                "text-align: center;"

    private fun logoSection(logoUrl: String): String =
        """<img src='$logoUrl' 
            alt='Company Logo' 
            style='max-width: 150px; 
            margin-bottom: 16px;'>"""

    private fun headerSection(recipientName: String): String =
        """
        <h1 style='font-size: 24px; color: #333; margin-bottom: 16px;'>
            Boucher de Préstamo
        </h1>
        <p style='font-size: 14px; color: #666; margin-bottom: 24px;'>
            Hola, <span style='font-weight: bold;'>$recipientName 👋</span>
        </p>
        <p style='font-size: 14px; color: #666; margin-bottom: 24px;'>
            Por favor, vea los detalles del pago a continuación:
        </p>
        """

    private fun paymentDetailsSection(params: ReceiptParams): String =
        """
        <div style='background-color: #f5f5f5; 
            padding: 24px; 
            border-radius: 16px; 
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); 
            text-align: left;'>
            <div style='margin-bottom: 16px;'>
                <span style='font-size: 14px; opacity: 0.7;'>Pagaste</span>
            </div>
            <div style='font-size: 36px; 
                font-weight: bold; 
                color: #007bff; 
                text-align: center;'>
                ${PADataConstants.CURRENCY}${params.amount}
            </div>
            ${dateSection(params.date)}
            ${dividerSection()}
            ${operationCodeSection(params.operationCode)}
        </div>
        """

    private fun dateSection(date: String): String =
        """
        <div style='display: flex; justify-content: space-between; padding: 8px 0;'>
            <div style='font-size: 14px; color: #333;'>
                <div style='font-weight: bold; color: #666;'>Fecha</div>
                <div style='font-size: 16px; font-weight: bold; margin-top: 4px;'>
                    $date
                </div>
            </div>
        </div>
        """

    private fun dividerSection(): String =
        """
        <div style='margin-top: 16px; 
            margin-bottom: 16px; 
            border-bottom: 1px solid #e0e0e0;'>
        </div>
        """

    private fun operationCodeSection(operationCode: String): String =
        """
        <div style='display: flex; justify-content: space-between; padding: 8px 0;'>
            <div style='font-size: 14px; color: #333;'>
                <div style='font-weight: bold; color: #666;'>
                    Código de Operación
                </div>
                <div style='font-size: 16px; font-weight: bold; margin-top: 4px;'>
                    $operationCode
                </div>
            </div>
        </div>
        """
}
