package com.paparazziapps.pretamistapp.modulos.dashboard.adapters

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.ContentPrestamoBinding
import com.paparazziapps.pretamistapp.databinding.ContentTitlePrestamoBinding
import com.paparazziapps.pretamistapp.domain.DelayCalculator
import com.paparazziapps.pretamistapp.helper.*
import com.paparazziapps.pretamistapp.modulos.dashboard.interfaces.setOnClickedPrestamo
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.domain.PaymentScheduled
import com.paparazziapps.pretamistapp.domain.PaymentScheduledEnum
import com.paparazziapps.pretamistapp.domain.TypePrestamo
import java.text.SimpleDateFormat
import java.util.*

class PrestamoAdapter(var setOnClickedPrestamo: setOnClickedPrestamo) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var prestamosList: MutableList<LoanDomain> = mutableListOf()
    var fechaActual:String
    //Time peru [Tiempo Actual]
    var fecha = SimpleDateFormat("dd/MM/yyyy").apply {
        timeZone = TimeZone.getTimeZone("GMT-5")
        format(Date()).toString().also {
            fechaActual = it
        }
    }

    fun setData(listLoanRespons: MutableList<LoanDomain>) {
        prestamosList = listLoanRespons
        notifyDataSetChanged()
    }

    fun updateItem(position: Int, loanDomain: LoanDomain) {
        prestamosList[position] = loanDomain
        notifyItemChanged(position)
    }

    fun removeItem(position: Int) {
        prestamosList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
                TypePrestamo.TITLE.value->{
                    ViewHolderTitle(parent.inflate(R.layout.content_title_prestamo))
                }
                else->{
                    ViewHolder(parent.inflate(R.layout.content_prestamo))
                }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(prestamosList[position].type){
            0-> TypePrestamo.TITLE.value
            else-> TypePrestamo.CARD.value
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as PrestamoViewHolder).bindView(prestamosList[position],fechaActual,setOnClickedPrestamo)
    }

    override fun getItemCount(): Int {
        return prestamosList.size
    }

    interface PrestamoViewHolder {
        fun bindView(
            item: LoanDomain,
            fechaActual: String,
            setOnClickedPrestamo: setOnClickedPrestamo
        )
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), PrestamoViewHolder {

        val binding = ContentPrestamoBinding.bind(itemView)

        override fun bindView(
            item: LoanDomain,
            fechaActual: String,
            setOnClickedPrestamo: setOnClickedPrestamo
        ) {
            val nombreCompleto = binding.nombreCompleto
            val numero_dias_retrasados = binding.numeroDiasRetrasados
            val lblDiasRetrasados = binding.lblDiasRetrasados
            var montoTotalAPagar:Double? = null
            var diasRetraso:Int = 10

            itemView.apply {

                //Set name
                nombreCompleto.setText("${replaceFirstCharInSequenceToUppercase(item.nombres.toString().trim())}, ${replaceFirstCharInSequenceToUppercase(item.apellidos.toString().trim())}")
                //Set amount to pay
                val amountToPayDailyWithCurrency = "${context.getString(R.string.tipo_moneda)} ${item.montoDiarioAPagar}"
                binding.amountToPayValue.text = amountToPayDailyWithCurrency

                //Set type of loan
                val typeLoanDisplayName = PaymentScheduled.getPaymentScheduledById(item.typeLoan?: INT_DEFAULT).displayName
                binding.typeOfLoanLabel.text = typeLoanDisplayName

                //Metodo para el calculo de dias retrasados
                //calcularDiasRetrasados(itemView, numero_dias_retrasados, item, binding.cardviewDiasRetrasadosV2, fechaActual)

                //set the delay for the type of loan
                val delay = calculateDelayForTypeLoan(item)
                updateDelayForLoanTypeInDays(item, delay) //set the delay for the type of loan if the loan is daily set "<DAYS> días retrasados" only set days for every type of loan
                updateUIForDelay(delay)

                //set the missing days for the type of loan
                val daysMissing = calculateTheMissingDaysToPayForTypeLoan(item)
                updatePendingLoanTypeInDaysOrQuotas(item, daysMissing)

                //setDiasRestantesPorPagar(item)

                //Enviar mensaje a whatsapp
                binding.btnSendWhatsapp.apply {
                    setOnClickListener {
                        try {
                            //calcular el monto total a pagar
                            diasRetraso = numero_dias_retrasados.text.toString().toInt()
                            montoTotalAPagar = getDoubleWithOneDecimalsReturnDouble((diasRetraso * item.montoDiarioAPagar!!))

                            //Mensaje
                            val msj = "Hola *${nombreCompleto.text}*, te escribimos para recordarte que tienes *${diasRetraso} ${lblDiasRetrasados.text}* " +
                                    "con los pagos de tu préstamo con un monto total a pagar de: *${context.getString(R.string.tipo_moneda)}$montoTotalAPagar*"
                            openWhatsapp(item.celular, msj)
                        }catch (t:Throwable) {
                            Firebase.crashlytics.recordException(t)
                        }

                    }
                }

                //Actualizar Pago al hacer click al itemview
                setOnClickListener {
                    //calcular el monto total a pagar
                    diasRetraso = numero_dias_retrasados.text.toString().toInt()
                    montoTotalAPagar = getDoubleWithTwoDecimalsReturnDouble(diasRetraso * (item.capital?.toDouble()?.div(item.plazo_vto_in_days!!)!!))

                    if(numero_dias_retrasados.text.toString().toInt() == 0) {
                        println("numero de dias retrasados es cero: ${numero_dias_retrasados.text}")
                        setOnClickedPrestamo.actualizarPagoPrestamo(item, false, 0.0, adapterPosition, numero_dias_retrasados.text.toString())
                    }else {
                        println("monto total a pagar: ${montoTotalAPagar}")
                        setOnClickedPrestamo.actualizarPagoPrestamo(item, true, montoTotalAPagar?:0.0, adapterPosition,numero_dias_retrasados.text.toString())
                    }
                }
            }
        }

        private fun updatePendingLoanTypeInDaysOrQuotas(item: LoanDomain, daysMissing: Int) {
            val tyLoan = PaymentScheduled.getPaymentScheduledById(item.typeLoan ?: INT_DEFAULT)
            Log.d("MissingDays", "Missing days or quotes: $daysMissing")

            if (daysMissing < 0) {
                binding.numeroDiasPorPagar.text = ""
                binding.lblDiasPorPagar.text = "Préstamo vencido"
                binding.numeroDiasPorPagar.isVisible = false
                binding.cardviewDiasPorPagar.backgroundTintList = ContextCompat.getColorStateList(ctx, R.color.color_red_trasparente)
                return
            }

            when (tyLoan) {
                PaymentScheduledEnum.DAILY -> {
                    val daysPaid = item.diasPagados ?: 0
                    val missingText = if (daysPaid == 1) "día pagado" else "días pagados"
                    binding.lblDiasPorPagar.text = missingText
                    binding.numeroDiasPorPagar.text = daysMissing.toString()
                }
                else -> {
                    val quotesPaid = item.quotasPaid?:0
                    val quotas = if (quotesPaid == 1) "cuota pagada" else "cuotas pagadas"
                    Log.d("QuotasPending", "Quotas: $quotesPaid")

                    binding.numeroDiasPorPagar.text = formatQuotasRange(quotesPaid,item.quotas ?: 0)
                    binding.lblDiasPorPagar.text = quotas
                }
            }
        }

        private fun formatQuotasRange(currentQuota: Int, totalQuotas: Int): String {
            return "$currentQuota/$totalQuotas"
        }

        private fun updateUIForDelay(delay: Int) {
            if (delay <= 0) {
                binding.btnSendWhatsapp.isVisible = false
                binding.cardviewDiasRetrasadosV2.backgroundTintList = ContextCompat.getColorStateList(ctx, R.color.colorPrimary)
            } else {
                binding.btnSendWhatsapp.isVisible = true
                binding.cardviewDiasRetrasadosV2.backgroundTintList = ContextCompat.getColorStateList(ctx, R.color.red)
            }
        }

        private fun setDiasRestantesPorPagar(item: LoanDomain) {

            val diasEnQueTermina = getDiasRestantesFromStart(item.fecha_start_loan?:"",item.plazo_vto_in_days?:0)

            if(diasEnQueTermina < 0) {
                binding.numeroDiasPorPagar.setText("0") // Dias por pagar es igual o menor a cero entonces Prestamó vencido - completado
            }else{
                binding.numeroDiasPorPagar.apply { setText(diasEnQueTermina.toString()) }
            }


            binding.lblDiasPorPagar.apply {
                text = when {
                    diasEnQueTermina == 1 -> "día por pagar"
                    else -> "días por pagar "
                }
            }
        }

        private fun calculateTheMissingDaysToPayForTypeLoan(loan: LoanDomain): Int {
            val tyLoan = PaymentScheduled.getPaymentScheduledById(loan.typeLoan ?: INT_DEFAULT)
            val result = when (tyLoan) {
                PaymentScheduledEnum.DAILY -> (loan.plazo_vto_in_days ?: 0) - (loan.diasPagados ?: 0)
                else -> (loan.quotas ?: 0) - (loan.quotasPaid ?: 0)
            }
            return result
        }

        private fun openWhatsapp(celular: String?, msj: String) {
            //NOTE : please use with country code first 2digits without plus signed
            try {

                var msg = "Its Working"
                ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=${ctx.getString(R.string.codigo_pais)}" + celular + "&text=" + msj)).apply {
                    setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }catch (t: Throwable){
                //whatsapp app not install
                println("Error whatsapp: $t")
            }
        }

        private fun calculateDelayForTypeLoan(item: LoanDomain): Int {
            val tyLoan = PaymentScheduled.getPaymentScheduledById(item.typeLoan ?: INT_DEFAULT)
            val calculatorDelay = DelayCalculator()
            val daysDelayed = if (item.fechaUltimoPago.isNullOrEmpty()) {
                Log.d("FechaUltimoPago", "Fecha ultimo pago vacia")
                getDiasRestantesFromDateToNow(item.fecha_start_loan ?: "").toIntOrNull() ?: 0
            } else {
                Log.d("FechaUltimoPago", "Fecha ultimo pago: ${item.fechaUltimoPago}")
                getDiasRestantesFromDateToNowMinusDiasPagados(item.fecha_start_loan ?: "", item.diasPagados ?: 0).toIntOrNull() ?: 0
            }
            Log.d("DaysDelayed", "Days delayed: $daysDelayed")
            return calculatorDelay.calculateDelay(tyLoan, daysDelayed)
        }

        //set the delay for the type of loan if the loan is daily set "<DAYS> días retrasados" else set "<WEEKS> semanas retrasadas" and so on

        private fun updateDelayForLoanType(item: LoanDomain, delay: Int) {
            val tyLoan = PaymentScheduled.getPaymentScheduledById(item.typeLoan ?: INT_DEFAULT)
            val delayText = when (tyLoan) {
                PaymentScheduledEnum.DAILY -> if (delay == 1) "día retrasado" else " días retrasados"
                PaymentScheduledEnum.WEEKLY -> if (delay == 1) " semana retrasada" else " semanas retrasadas"
                PaymentScheduledEnum.FORTNIGHTLY -> if (delay == 1) " quincena retrasada" else " quincenas retrasadas"
                PaymentScheduledEnum.MONTHLY -> if (delay == 1) " mes retrasado" else " meses retrasados"
                PaymentScheduledEnum.BIMONTHLY -> if (delay == 1) " bimestre retrasado" else " bimestres retrasados"
                PaymentScheduledEnum.QUARTERLY -> if (delay == 1) " trimestre retrasado" else " trimestres retrasados"
                PaymentScheduledEnum.SEMIANNUAL -> if (delay == 1) " semestre retrasado" else " semestres retrasados"
                PaymentScheduledEnum.ANNUAL -> if (delay == 1) " año retrasado" else " años retrasados"
                else -> ""
            }
            binding.numeroDiasRetrasados.text = delay.toString()
            binding.lblDiasRetrasados.text = delayText
        }

        ////set the delay for the type of loan if the loan is daily set "<DAYS> días retrasados" only set days for every type of loan

        private fun updateDelayForLoanTypeInDays(item: LoanDomain, delay: Int) {
            val tyLoan = PaymentScheduled.getPaymentScheduledById(item.typeLoan ?: INT_DEFAULT)
            val delayText = if (delay == 1) "día retrasado" else " días retrasados"
            binding.numeroDiasRetrasados.text = delay.toString()
            binding.lblDiasRetrasados.text = delayText
        }

    }



    class ViewHolderTitle(itemView: View) : RecyclerView.ViewHolder(itemView), PrestamoViewHolder {

        val binding = ContentTitlePrestamoBinding.bind(itemView)

        override fun bindView(
            item: LoanDomain,
            fechaActual: String,
            setOnClickedPrestamo: setOnClickedPrestamo
        ) {
            var title = binding.title
            itemView.apply {
                title.text = item.title
            }
        }
    }

}



