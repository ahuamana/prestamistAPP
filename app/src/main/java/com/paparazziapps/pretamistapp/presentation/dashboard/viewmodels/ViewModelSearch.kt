package com.paparazziapps.pretamistapp.presentation.dashboard.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObject
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.application.MyPreferences
import com.paparazziapps.pretamistapp.data.PADataConstants
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.repository.PAAnalyticsRepository
import com.paparazziapps.pretamistapp.data.repository.PAEmailRepository
import com.paparazziapps.pretamistapp.data.repository.PARepository
import com.paparazziapps.pretamistapp.domain.DelayCalculator
import com.paparazziapps.pretamistapp.domain.DetailLoanDomain
import com.paparazziapps.pretamistapp.domain.InformationReceiptDomain
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.domain.PaymentScheduled
import com.paparazziapps.pretamistapp.domain.PaymentScheduledEnum
import com.paparazziapps.pretamistapp.domain.utils.convertUnixTimeToFormattedDate
import com.paparazziapps.pretamistapp.helper.INT_DEFAULT
import com.paparazziapps.pretamistapp.helper.getDiasRestantesFromDateToNow
import com.paparazziapps.pretamistapp.helper.getDiasRestantesFromDateToNowMinusDiasPagados
import com.paparazziapps.pretamistapp.helper.getFechaActualNormalCalendar
import com.paparazziapps.pretamistapp.helper.replaceFirstCharInSequenceToUppercase
import com.paparazziapps.pretamistapp.presentation.dashboard.viewmodels.ViewModelDashboard.DashboardState
import com.paparazziapps.pretamistapp.presentation.dashboard.views.LoanListHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

class ViewModelSearch(
    private val repository: PARepository,
    private val preferences: MyPreferences,
    private val analyticsRepository: PAAnalyticsRepository,
    private val paEmailRepository: PAEmailRepository,
) : ViewModel(){

    private val tag = ViewModelSearch::class.java.simpleName
    private val _state = MutableStateFlow(SearchState.idle())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val _state2 = MutableStateFlow(DashboardState.idle())
    val state2: StateFlow<DashboardState> = _state2.asStateFlow()

    private val allLoans = mutableListOf<LoanDomain>()

    init {
        loadLoans()
    }



    private fun loadLoans() = viewModelScope.launch {
        _state.value = _state.value.copy(state = SearchEvent.LOADING)
        when(val result = repository.getLoans()){
            is PAResult.Error -> {
                Log.d(tag,"ViewModelDashboard --> : Error ${result.exception.message}")
                _state.value = _state.value.copy(state = SearchEvent.ERROR, message = result.exception.message)
            }
            is PAResult.Success -> {
                Log.d(tag,"ViewModelDashboard --> : Success ${result.data.count()}")
                if(result.data.isEmpty) {
                    Log.d(tag," lista prestamos esta vacia")
                    _state.value = _state.value.copy(state = SearchEvent.EMPTY)
                    return@launch
                }
                val loans = result.data.mapNotNull { document->
                    document.toObject<LoanDomain>()
                }
                val manager = LoanListHandler(preferences)
                val processedLoans = manager.processLoans(loans)
                allLoans.clear()
                allLoans.addAll(processedLoans)

                _state.value = SearchState.success(processedLoans)
            }
        }
    }


    data class SearchState(
        val state : SearchEvent,
        val loans: List<LoanDomain>? = null,
        val message: String? = null,
        val dialogState: SearchDialogState = SearchDialogState.None,
        val informationReceipt: InformationReceiptDomain? = null
    ){
        companion object {
            fun idle() = SearchState(SearchEvent.LOADING)
            fun loading() = SearchState(SearchEvent.LOADING)
            fun success(loans: List<LoanDomain>) = SearchState(SearchEvent.SUCCESS, loans)
            fun error(message: String) = SearchState(SearchEvent.ERROR, message = message)
        }
    }

    fun processIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.LoadLoans -> loadLoans()
            is SearchIntent.UpdateLoan -> updateLoan(
                loanDomain = intent.loanDomain,
                quotesToPay = intent.quotesToPay,
                currentDate = getFechaActualNormalCalendar()
            )
            is SearchIntent.CloseLoan -> closeLoan(intent.loanId)
            SearchIntent.ResetStatusDialogs -> {
                resetStatusDialogs()
            }

            is SearchIntent.SendMessageToOtherApp ->{
                sendMessageToOtherApp(intent.loanDomain, context = intent.context)
            }

            is SearchIntent.SendMessageToWhatsApp -> {
                sendMessageToWhatsApp(intent.loanDomain, context = intent.context)
            }
            is SearchIntent.SearchLoanByName -> {
                val currentLoans = _state.value.loans ?: emptyList()
                val filteredLoans = if (intent.name.isBlank()) {
                    currentLoans
                } else {
                    currentLoans.filter { loan ->
                        val fullName = "${loan.names?.trim()} ${loan.lastnames?.trim()}".lowercase()
                        fullName.contains(intent.name.trim().lowercase())
                    }
                }
                if (filteredLoans.isEmpty()) {
                    _state.value = _state.value.copy(state = SearchEvent.EMPTY, loans = filteredLoans)
                } else {
                    _state.value = _state.value.copy(state = SearchEvent.SUCCESS, loans = filteredLoans)
                }
            }

            is SearchIntent.SearchLoanByNextPayment -> {
                handledSearchByNextPayment(intent.date)
            }
        }
    }

    private  val DAY_MS = 86_400_000L // milliseconds in a day

    private fun handledSearchByNextPayment(targetDate: Long) {
        val currentLoans =  allLoans

        val filteredLoans = if (targetDate == 0L) {
            currentLoans // show all if no date selected
        } else {
            currentLoans.filter { loan ->
                // Skip fully paid loans
                val totalQuotes = loan.quotas ?: return@filter false
                val quotesPaid = loan.quotasPaid ?: 0
                if (quotesPaid >= totalQuotes) return@filter false

                // Get type of loan (daily, weekly, etc.)
                val tyLoan = PaymentScheduled.getPaymentScheduledById(loan.typeLoan ?: INT_DEFAULT)

                // Calculate next payment date
                val nextPaymentDate = loan.loanStartDateUnix?.plus(((quotesPaid + 1) * tyLoan.days * DAY_MS)) ?: return@filter false

                // Compare only by calendar day (ignore hours)
                isSameDay(nextPaymentDate, targetDate)
            }
        }

        _state.value = _state.value.copy(
            state = if (filteredLoans.isEmpty()) SearchEvent.EMPTY else SearchEvent.SUCCESS,
            loans = filteredLoans
        )
    }

    /**
     * Returns true if both dates fall on the same calendar day (local timezone).
     */
    private fun isSameDay(date1: Long, date2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = date1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun sendMessageToWhatsApp(loanDomain: LoanDomain, context: Context) {
        val namesWithUpperCase = replaceFirstCharInSequenceToUppercase(loanDomain.names?.trim() ?: "")
        val lastnamesWithUpperCase = replaceFirstCharInSequenceToUppercase(loanDomain.lastnames?.trim() ?: "")
        val fullName = "$namesWithUpperCase, $lastnamesWithUpperCase"
        val phone = loanDomain.cellular ?: ""
        val quotesPending = loanDomain.quotasPending ?: loanDomain.quotas ?: 0

        val delay = calculateDelayForTypeLoan(loanDomain)
        val delayText = if (delay == 1) "día retrasado" else " días retrasados"

        val message = if (delay > 0) {
            // Si hay retraso, incluir los días de retraso en el mensaje
            "Hola ${fullName}, hemos registrado el pago de ${loanDomain.quotasPaid ?: 0} de ${loanDomain.quotas} cuota(s) pagadas. " +
                    "Te informamos que aún faltan $quotesPending cuota(s) por pagar de tu préstamo. " +
                    "Lamentablemente, tu pago está $delay $delayText. Te sugerimos ponerte al día lo antes posible para evitar cargos adicionales."
        } else {
            // Si no hay retraso, solo información sobre el saldo pendiente
            "Hola ${fullName}, hemos registrado el pago de ${loanDomain.quotasPaid ?: 0} de ${loanDomain.quotas} cuota(s) pagadas. " +
                    "Te informamos que aún faltan $quotesPending cuota(s) por pagar de tu préstamo. " +
                    "¡Gracias por mantenerte al día con tus pagos!"
        }

        val uri = Uri.parse("https://api.whatsapp.com/send?phone=${context.getString(R.string.codigo_pais)}" + phone + "&text=" + message)

        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)

        _state.value = _state.value.copy(dialogState = SearchDialogState.SuccessSendMessage)
    }

    private fun resetStatusDialogs() {
        _state.value = _state.value.copy(dialogState = SearchDialogState.None)
    }

    private fun sendMessageToOtherApp(
        loanDomain: LoanDomain,
        context: Context) = viewModelScope.launch {

        _state.value = _state.value.copy(dialogState = SearchDialogState.Loading)
        val namesWithUpperCase = replaceFirstCharInSequenceToUppercase(loanDomain.names?.trim() ?: "")
        val lastnamesWithUpperCase = replaceFirstCharInSequenceToUppercase(loanDomain.lastnames?.trim() ?: "")
        val fullName = "$namesWithUpperCase, $lastnamesWithUpperCase"
        val phone = loanDomain.cellular ?: ""
        val quotesPending = loanDomain.quotasPending ?: loanDomain.quotas ?: 0

        val delay = calculateDelayForTypeLoan(loanDomain)
        val delayText = if (delay == 1) "día retrasado" else " días retrasados"

        val message = if (delay > 0) {
            // Si hay retraso, incluir los días de retraso en el mensaje
            "Hola ${fullName}, hemos registrado el pago de ${loanDomain.quotasPaid ?: 0} de ${loanDomain.quotas} cuota(s) pagadas. " +
                    "Te informamos que aún faltan $quotesPending cuota(s) por pagar de tu préstamo. " +
                    "Lamentablemente, tu pago está $delay $delayText. Te sugerimos ponerte al día lo antes posible para evitar cargos adicionales."
        } else {
            // Si no hay retraso, solo información sobre el saldo pendiente
            "Hola ${fullName}, hemos registrado el pago de ${loanDomain.quotasPaid ?: 0} de ${loanDomain.quotas} cuota(s) pagadas. " +
                    "Te informamos que aún faltan $quotesPending cuota(s) por pagar de tu préstamo. " +
                    "¡Gracias por mantenerte al día con tus pagos!"
        }

        try {
            val smsManager = context.getSystemService(SmsManager::class.java)
            val dividedMessage = smsManager.divideMessage(message)
            val codeCountry = context.getString(R.string.codigo_pais)
            smsManager.sendMultipartTextMessage(codeCountry+phone, null, dividedMessage, null, null)
            _state.value = _state.value.copy(dialogState = SearchDialogState.SuccessSendMessage)
        }catch (e: Exception){
            Log.d(tag, "Error al enviar mensaje: ${e.message}")
            _state.value = _state.value.copy(dialogState = SearchDialogState.Error(e.message ?: ""))
        }
    }

    private fun calculateDelayForTypeLoan(item: LoanDomain): Int {
        val tyLoan = PaymentScheduled.getPaymentScheduledById(item.typeLoan ?: INT_DEFAULT)
        val calculatorDelay = DelayCalculator()
        val daysDelayed = if (item.lastPaymentDate.isNullOrEmpty()) {
            Log.d("lastPaymentDate", "Fecha ultimo pago vacia")
            getDiasRestantesFromDateToNow(item.loanStartDateFormatted ?: "").toIntOrNull() ?: 0
        } else {
            Log.d("lastPaymentDate", "Fecha ultimo pago: ${item.lastPaymentDate}")
            getDiasRestantesFromDateToNowMinusDiasPagados(item.loanStartDateFormatted ?: "", item.quotasPaid ?: 0).toIntOrNull() ?: 0
        }
        Log.d("DaysDelayed", "Days delayed: $daysDelayed")

        val pendingQuotes = item.quotas?.minus(item.quotasPaid ?: 0) ?: 0
        Log.d("PendingQuotes", "Pending quotes: $pendingQuotes")

        if(daysDelayed<= 0) return 0
        if(pendingQuotes <= 0) return 0

        return calculatorDelay.calculateDelay(tyLoan, daysDelayed)
    }

    private fun updateLoan(
        loanDomain: LoanDomain,
        quotesToPay: Int,
        currentDate: String,
    ) {
        //Daily payment
        val idLoan = loanDomain.id ?: "" //Works for daily and other
        val needToClose = (loanDomain.quotasPending == (loanDomain.quotas?:0)) // works for daily and other
        val totalAmountToPay = quotesToPay.times(loanDomain.amountPerQuota ?: 0.0) // rename to quotaAmount // works for daily and other

        //Other payment
        val quotesPaidBefore = loanDomain.quotasPaid ?: 0
        val quotesPaidNew = quotesPaidBefore + quotesToPay

        val quotesPendingBefore = loanDomain.quotasPending ?: loanDomain.quotas ?: 0
        val quotesPendingNew =  quotesPendingBefore - quotesToPay

        //Create Recipe domain


        if (needToClose) {
            processIntent(SearchIntent.CloseLoan(idLoan))
        } else {
            updatePayment(
                loanDomain= loanDomain,
                currentDate = currentDate ,
                quotesPaidNew = quotesPaidNew,
                quotesPendingNew = quotesPendingNew,
                totalAmountToPay = totalAmountToPay
            )
        }
    }

    private fun updatePayment(  loanDomain: LoanDomain,
                                currentDate: String,
                                quotesPaidNew: Int,
                                quotesPendingNew: Int,
                                totalAmountToPay: Double) = viewModelScope.launch {
        val typeLoan = PaymentScheduled.getPaymentScheduledById(loanDomain.typeLoan ?: INT_DEFAULT)
        when (typeLoan) {
            PaymentScheduledEnum.DAILY -> {
                handledPaymentDaily(loanDomain,currentDate,quotesPaidNew,quotesPendingNew,totalAmountToPay)
            }
            else -> {
                handledOtherPayment(loanDomain,currentDate,quotesPaidNew,quotesPendingNew,totalAmountToPay)
            }
        }
    }

    private fun handledOtherPayment(
        loanDomain: LoanDomain,
        currentDate: String,
        quotesPaidNew: Int,
        quotesPendingNew: Int,
        totalAmountToPay: Double
    ) = viewModelScope.launch {

        //PaidDays * the type loan days times the quotas
        _state.value = _state.value.copy(dialogState = SearchDialogState.Loading)

        val result = repository.setLastPaymentForQuota(
            loanDomain.id ?: "",
            currentDate,
            quotesPendingNew,
            quotesPaidNew
        )

        when (result) {
            is PAResult.Error -> {
                Log.d(tag, "ViewModelRegister --> : Error ${result.exception.message}")
                _state.value = _state.value.copy(dialogState = SearchDialogState.Error(result.exception.message ?: ""))
            }

            is PAResult.Success -> {
                Log.d(tag, "ViewModelRegister --> : Success ${result.data}")
                handledDetailPayment(loanDomain, quotesPaidNew, quotesPendingNew, totalAmountToPay)
            }
        }
    }

    private fun handledPaymentDaily(
        loanDomain: LoanDomain,
        currentDate: String,
        quotesPaidNew: Int,
        quotesPendingNew: Int,
        totalAmountToPay: Double,
    ) = viewModelScope.launch {

        _state.value = _state.value.copy(dialogState = SearchDialogState.Loading)
        val result = repository.setLastPayment(
            loanDomain.id ?: "",
            currentDate,
            quotesPendingNew,
            quotesPaidNew
        )
        when (result) {
            is PAResult.Error -> {
                Log.d(tag, "ViewModelRegister --> : Error ${result.exception.message}")
                _state.value = _state.value.copy(dialogState = SearchDialogState.Error(result.exception.message ?: ""))
            }

            is PAResult.Success -> {
                Log.d(tag, "ViewModelRegister --> : Success ${result.data}")
                handledDetailPayment(loanDomain, quotesPaidNew, quotesPendingNew, totalAmountToPay)
            }
        }
    }

    private fun closeLoan(idLoan:String) = viewModelScope.launch {
        _state.value = _state.value.copy(dialogState = SearchDialogState.Loading)
        val result = repository.closeLoan(idLoan)
        when(result){
            is PAResult.Error -> {
                Log.d(tag,"ViewModelRegister --> : Error ${result.exception.message}")
                _state.value = _state.value.copy(dialogState = SearchDialogState.ErrorCloseLoan)
                removeItemFromListDeepCopy(idLoan)
            }
            is PAResult.Success -> {
                _state.value = _state.value.copy(dialogState = SearchDialogState.SuccessCloseLoan)
            }
        }
    }

    private fun removeItemFromListDeepCopy(loanId: String) {
        val currentState = _state.value
        val currentLoans = currentState.loans ?: return

        // Create new list with completely new object instances
        val newLoans = currentLoans
            .filter { it.id != loanId }
            .map { it.copy() } // Assuming Loan is a data class with copy()
            .toList()

        _state.value = currentState.copy(loans = newLoans)
    }
    private fun handledDetailPayment(
        loanDomain: LoanDomain,
        quotesPaidNew: Int,
        quotesPendingNew: Int,
        totalAmountToPay: Double,
    ) = viewModelScope.launch {

        val detail = DetailLoanDomain(
            idLoan = loanDomain.id ?: "",
            totalAmountToPay = totalAmountToPay
        )

        when (val resultDetail = repository.createDetail(detail)) {
            is PAResult.Error -> {
                Log.d(tag, "--> : Error ${resultDetail.exception.message}")
                _state.value = _state.value.copy(dialogState = SearchDialogState.Error(resultDetail.exception.message ?: ""))
            }

            is PAResult.Success -> {
                //_message.value = "Se actualizo el pago"
                Log.d(tag, "--> : Success ${resultDetail.data}")

                //Here also update the loans list with the new data changed
                val idLoan = resultDetail.data.idLoan
                val currentLoan = _state.value.loans?.firstOrNull { it.id == idLoan }
                val newLoan = currentLoan?.copy(
                    lastPaymentDate = resultDetail.data.paymentDate,
                    quotasPaid = quotesPaidNew,
                    quotasPending = quotesPendingNew
                )

                val newLoans = _state.value.loans?.mapNotNull { loan ->
                    if (loan.id == idLoan) { newLoan } else { loan }
                }

                val namesWithUpperCase = replaceFirstCharInSequenceToUppercase(loanDomain.names?.trim() ?: "")
                val lastnamesWithUpperCase = replaceFirstCharInSequenceToUppercase(loanDomain.lastnames?.trim() ?: "")
                val fullName = "$namesWithUpperCase, $lastnamesWithUpperCase"

                val informationReceipt = InformationReceiptDomain(
                    idReceipt = resultDetail.data.idDetailLoan,
                    codeOperation = resultDetail.data.codeOperation,
                    fullName = fullName,
                    phoneNumber = loanDomain.cellular ?: "",
                    names = loanDomain.names ?: "",
                    lastNames = loanDomain.lastnames ?: "",
                    quotes = loanDomain.quotas ?: 0,
                    quotesPaidNew = quotesPaidNew,
                    totalAmountToPay = totalAmountToPay,
                    amountPerQuote = loanDomain.amountPerQuota ?: 0.0,
                    typeLoan = loanDomain.typeLoan ?: INT_DEFAULT,
                    loanStartDateUnix = loanDomain.loanStartDateUnix ?: 0,
                    email = loanDomain.email ?: ""
                )
                sendEmailReceipt(informationReceipt)

                _state.value = _state.value.copy(dialogState = SearchDialogState.SuccessUpdateLoan, loans = newLoans, informationReceipt = informationReceipt)
            }
        }
    }

    private fun sendEmailReceipt(informationReceipt: InformationReceiptDomain) = viewModelScope.launch {
        val result = paEmailRepository.sendPaymentReceipt(
            recipientEmail = informationReceipt.email,
            amount = informationReceipt.totalAmountToPay,
            date = convertUnixTimeToFormattedDate(informationReceipt.codeOperation),
            operationCode = informationReceipt.codeOperation.toString(),
            recipientName = informationReceipt.names
        )
        when (result) {
            is PAResult.Error -> {
                Log.d(tag, "ViewModelDashboard -->sendEmail: Error ${result.exception.message}")
                analyticsRepository.logEvent(PADataConstants.EVENT_RESEND_EMAIL_ERROR)
            }

            is PAResult.Success -> {
                Log.d(tag, "ViewModelDashboard -->sendEmail : Success ${result.data}")
                analyticsRepository.logEvent(PADataConstants.EVENT_RESEND_EMAIL_SUCCESS)
            }
        }
    }

    fun logEvent(event: String) {
        analyticsRepository.logEvent(event)
    }

    fun getInformationReceipt(): InformationReceiptDomain? {
        return _state.value.informationReceipt
    }


    sealed class SearchIntent {
        data object LoadLoans : SearchIntent()
        data class UpdateLoan(
            val loanDomain: LoanDomain,
            val quotesToPay: Int,
        ) : SearchIntent()
        data class CloseLoan(val loanId: String) : SearchIntent()
        data object ResetStatusDialogs : SearchIntent()
        data class SendMessageToOtherApp(val loanDomain: LoanDomain, val context: Context) : SearchIntent()
        data class SendMessageToWhatsApp(val loanDomain: LoanDomain, val context: Context) : SearchIntent()

        data class SearchLoanByName(val name: String): SearchIntent()

        data class SearchLoanByNextPayment(val date:Long): SearchIntent()
    }

    sealed class SearchDialogState {
        data object None : SearchDialogState() // No dialog is showing
        data object Loading : SearchDialogState() // Loading dialog is active
        data class Error(val message: String) : SearchDialogState() // Error dialog with a message
        data object SuccessCloseLoan : SearchDialogState() // Success dialog for loan closure
        data object SuccessUpdateLoan : SearchDialogState() // Success dialog for loan update
        data object SuccessSendMessage : SearchDialogState() // Success dialog for message sent
        data object ErrorCloseLoan : SearchDialogState() // Error dialog for loan closure
    }

    enum class SearchEvent {
        LOADING,
        SUCCESS,
        ERROR,
        EMPTY
    }
}