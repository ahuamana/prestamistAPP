package com.paparazziapps.pretamistapp.modulos.registro.pojo

data class PaymentScheduled(
    val id: Int,
    val name: String,
    val description: String,
    val paymentScheduled: PaymentScheduledEnum
) {
    companion object {
        fun getPaymentScheduledById(id: Int): PaymentScheduledEnum {
            return PaymentScheduledEnum.entries.firstOrNull { it.id == id } ?: PaymentScheduledEnum.DAILY
        }

        fun getPaymentScheduledByName(name: String): PaymentScheduledEnum {
            return PaymentScheduledEnum.entries.firstOrNull { it.displayName == name } ?: PaymentScheduledEnum.DAILY
        }

        fun getPaymentScheduledListString(): ArrayList<String> {
            return PaymentScheduledEnum.entries.map {
                it.displayName
            } as? ArrayList<String> ?: arrayListOf()
        }


    }
}

enum class PaymentScheduledEnum(val id: Int, val displayName: String, val days:Int) {
    DAILY(1, "Diario (cada día)", 1),
    WEEKLY(2, "Semanal (cada 7 días)", 7),
    FORTNIGHTLY(3, "Quincenal (cada 15 días)", 15),
    MONTHLY(4, "Mensual (cada 30 días)", 30),
    BIMONTHLY(5, "Bimestral (cada 60 días)", 60),
    QUARTERLY(6, "Trimestral (cada 90 días)", 90),
    SEMIANNUAL(7, "Semestral (cada 180 días)", 180),
    ANNUAL(8, "Anual (cada 365 días)", 365)
}