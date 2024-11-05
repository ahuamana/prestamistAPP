package com.paparazziapps.pretamistapp.presentation.dashboard.adapters

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.ContentPrestamoBinding
import com.paparazziapps.pretamistapp.databinding.ContentTitlePrestamoBinding
import com.paparazziapps.pretamistapp.domain.DelayCalculator
import com.paparazziapps.pretamistapp.helper.*
import com.paparazziapps.pretamistapp.presentation.dashboard.interfaces.SetOnClickedLoan
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.domain.PaymentScheduled
import com.paparazziapps.pretamistapp.domain.PaymentScheduledEnum
import com.paparazziapps.pretamistapp.domain.RandomColorGenerator
import com.paparazziapps.pretamistapp.domain.TypePrestamo
import java.text.SimpleDateFormat
import java.util.*

class LoanAdapter(private val listener: SetOnClickedLoan) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        (holder as PrestamoViewHolder).bindView(prestamosList[position],fechaActual,listener)
    }

    override fun getItemCount(): Int {
        return prestamosList.size
    }

    interface PrestamoViewHolder {
        fun bindView(
            item: LoanDomain,
            currentDate: String,
            listenerLoan: SetOnClickedLoan
        )
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), PrestamoViewHolder {

        val binding = ContentPrestamoBinding.bind(itemView)

        override fun bindView(
            item: LoanDomain,
            currentDate: String,
            listenerLoan: SetOnClickedLoan
        ) {
            val nombreCompleto = binding.nombreCompleto
            val numero_dias_retrasados = binding.numeroDiasRetrasados
            val lblDiasRetrasados = binding.lblDiasRetrasados
            var montoTotalAPagar:Double? = null
            var diasRetraso:Int = 10

            itemView.apply {
                val tag = LoanAdapter::class.java.simpleName
                //Set name
                nombreCompleto.setText("${replaceFirstCharInSequenceToUppercase(item.names.toString().trim())}, ${replaceFirstCharInSequenceToUppercase(item.lastnames.toString().trim())}")
                //Set amount to pay
                val amountToPayDailyWithCurrency = "${context.getString(R.string.tipo_moneda)} ${item.amountPerQuota}"
                binding.amountToPayValue.text = amountToPayDailyWithCurrency

                //Set type of loan
                val typeLoanDisplayName = PaymentScheduled.getPaymentScheduledById(item.typeLoan?: INT_DEFAULT).displayName
                binding.typeOfLoanLabel.text = typeLoanDisplayName

                //set the delay for the type of loan
                val delay = calculateDelayForTypeLoan(item)
                updateDelayForLoanTypeInDays(item, delay) //set the delay for the type of loan if the loan is daily set "<DAYS> días retrasados" only set days for every type of loan
                updateUIForDelay(delay)

                //set the missing days for the type of loan
                val daysMissing = calculateTheMissingDaysToPayForTypeLoan(item)
                updatePendingLoanTypeInDaysOrQuotas(item, daysMissing)


                //set text for image avatar
                val firstLetter = item.names?.firstOrNull()?.uppercase()
                val firstLetterLastname = item.lastnames?.firstOrNull()?.uppercase()
                binding.imgAvatarCircular.text  = "$firstLetter$firstLetterLastname"

                //set background color for image avatar from a random list of colors
                val color = RandomColorGenerator.getRandomColor()
                setRoundedAvatarBackground(binding.imgAvatarCircular, color)


                binding.btnSendMessage.setOnClickListener {
                    listenerLoan.sendMessageToWhatsapp(item)
                }

                binding.btnShare.setOnClickListener {
                    listenerLoan.sendMessageToOtherApp(item)
                }

                //Actualizar Pago al hacer click al itemview
                setOnClickListener {
                    //calcular el monto total a pagar
                    diasRetraso = numero_dias_retrasados.text.toString().toInt()
                    montoTotalAPagar = getDoubleWithTwoDecimalsReturnDouble(diasRetraso * (item.capital?.toDouble()?.div(item.quotas!!)!!))

                    if(numero_dias_retrasados.text.toString().toInt() == 0) {
                        Log.d(tag,"numero de dias retrasados es cero: ${numero_dias_retrasados.text}")
                        listenerLoan.updateLoanPaid(item, false, 0.0, adapterPosition, numero_dias_retrasados.text.toString())
                    }else {
                        Log.d(tag,"monto total a pagar: ${montoTotalAPagar}")
                        listenerLoan.updateLoanPaid(item, true, montoTotalAPagar?:0.0, adapterPosition,numero_dias_retrasados.text.toString())
                    }
                }
            }
        }

        private fun setRoundedAvatarBackground(textView: TextView, color: Int) {
            val drawable = GradientDrawable()
            drawable.shape = GradientDrawable.OVAL
            drawable.setColor(color)
            textView.background = drawable
        }


        private fun updatePendingLoanTypeInDaysOrQuotas(item: LoanDomain, daysMissing: Int) {
            val tyLoan = PaymentScheduled.getPaymentScheduledById(item.typeLoan ?: INT_DEFAULT)
            Log.d("MissingDays", "Missing days or quotes: $daysMissing")

            if (daysMissing < 0) {
                binding.numeroDiasPorPagar.text = ""
                binding.lblDiasPorPagar.text = "Préstamo vencido"
                binding.numeroDiasPorPagar.isVisible = false
                binding.cardviewDiasPorPagar.backgroundTintList = ContextCompat.getColorStateList(itemView.context, R.color.color_red_trasparente)
                return
            }

            when (tyLoan) {
                PaymentScheduledEnum.DAILY -> {
                    val daysPaid = item.quotasPaid ?: 0
                    val daysTotal = item.quotas ?: 0
                    val pendingDays = daysTotal - daysPaid
                    val missingText = if (pendingDays == 1) "día pagado" else "días pagados"
                    binding.lblDiasPorPagar.text = missingText
                    binding.numeroDiasPorPagar.text = formatPaidDaysRange(daysPaid, daysTotal)
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

        private fun formatPaidDaysRange(currentDays: Int, totalDays: Int): String {
            return "$currentDays/$totalDays"
        }

        private fun formatQuotasRange(currentQuota: Int, totalQuotas: Int): String {
            return "$currentQuota/$totalQuotas"
        }

        private fun updateUIForDelay(delay: Int) {
            if (delay <= 0) {
                binding.cardviewDiasRetrasadosV2.backgroundTintList = ContextCompat.getColorStateList(
                    itemView.context, R.color.colorPrimary)
            } else {
                binding.cardviewDiasRetrasadosV2.backgroundTintList = ContextCompat.getColorStateList(
                    itemView.context, R.color.red)
            }
        }

        private fun calculateTheMissingDaysToPayForTypeLoan(loan: LoanDomain): Int {
            val result = (loan.quotas ?: 0) - (loan.quotasPaid ?: 0)
            return result
        }

        private fun calculateDelayForTypeLoan(item: LoanDomain): Int {
            val tyLoan = PaymentScheduled.getPaymentScheduledById(item.typeLoan ?: INT_DEFAULT)
            val calculatorDelay = DelayCalculator()
            val daysDelayed = if (item.lastPaymentDate.isNullOrEmpty()) {
                Log.d("lastPaymentDate", "Fecha ultimo pago vacia")
                getDiasRestantesFromDateToNow(item.fecha_start_loan ?: "").toIntOrNull() ?: 0
            } else {
                Log.d("lastPaymentDate", "Fecha ultimo pago: ${item.lastPaymentDate}")
                getDiasRestantesFromDateToNowMinusDiasPagados(item.fecha_start_loan ?: "", item.quotasPaid ?: 0).toIntOrNull() ?: 0
            }
            Log.d("DaysDelayed", "Days delayed: $daysDelayed")

            val pendingQuotes = item.quotas?.minus(item.quotasPaid ?: 0) ?: 0
            Log.d("PendingQuotes", "Pending quotes: $pendingQuotes")

            if(pendingQuotes <= 0 || daysDelayed <=0) return 0

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
            currentDate: String,
            setOnClickedLoan: SetOnClickedLoan
        ) {
            val title = binding.title
            itemView.apply {
                title.text = item.title
            }
        }
    }

}



