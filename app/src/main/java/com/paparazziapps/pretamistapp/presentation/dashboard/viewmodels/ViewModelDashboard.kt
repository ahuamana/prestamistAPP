package com.paparazziapps.pretamistapp.presentation.dashboard.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObject
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.application.MyPreferences
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.helper.INT_DEFAULT
import com.paparazziapps.pretamistapp.helper.getFechaActualNormalInUnixtime
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.domain.PaymentScheduled
import com.paparazziapps.pretamistapp.domain.PaymentScheduledEnum
import com.paparazziapps.pretamistapp.data.repository.PARepository
import com.paparazziapps.pretamistapp.domain.DelayCalculator
import com.paparazziapps.pretamistapp.domain.DetallePrestamoSender
import com.paparazziapps.pretamistapp.domain.Sucursales
import com.paparazziapps.pretamistapp.domain.TypePrestamo
import com.paparazziapps.pretamistapp.helper.fromJson
import com.paparazziapps.pretamistapp.helper.getDiasRestantesFromDateToNow
import com.paparazziapps.pretamistapp.helper.getDiasRestantesFromDateToNowMinusDiasPagados
import com.paparazziapps.pretamistapp.helper.getFechaActualNormalCalendar
import com.paparazziapps.pretamistapp.helper.replaceFirstCharInSequenceToUppercase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViewModelDashboard (
    private val repository: PARepository,
    private val preferences: MyPreferences
) : ViewModel(){

    private val tag = ViewModelDashboard::class.java.simpleName
    private val _state = MutableStateFlow(DashboardState.idle())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        loadLoans()
    }

    fun processIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.LoadLoans -> loadLoans()
            is DashboardIntent.UpdateLoan -> updateLoan(
                loanDomain = intent.loanDomain,
                quotesToPay = intent.quotesToPay,
                currentDate = getFechaActualNormalCalendar()
            )
            is DashboardIntent.CloseLoan -> closeLoan(intent.loanId)
            DashboardIntent.ResetStatusDialogs -> {
                resetStatusDialogs()
            }

            is DashboardIntent.SendMessageToOtherApp ->{
                sendMessageToOtherApp(intent.loanDomain, context = intent.context)
            }

            is DashboardIntent.SendMessageToWhatsApp -> {
                sendMessageToWhatsApp(intent.loanDomain, context = intent.context)

            }
        }
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

        if(daysDelayed<= 0) return 0
        if(pendingQuotes <= 0) return 0

        return calculatorDelay.calculateDelay(tyLoan, daysDelayed)
    }

    private fun sendMessageToOtherApp(
        loanDomain: LoanDomain,
        context: Context) = viewModelScope.launch {

        TODO("Implement this method")
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

        if (needToClose) {
            processIntent(DashboardIntent.CloseLoan(idLoan))
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


    private fun loadLoans() = viewModelScope.launch {
        _state.value = _state.value.copy(state = DashboardEvent.LOADING)
        when(val result = repository.getLoans()){
            is PAResult.Error -> {
                Log.d(tag,"ViewModelDashboard --> : Error ${result.exception.message}")
                _state.value = _state.value.copy(state = DashboardEvent.ERROR, message = result.exception.message)
            }
            is PAResult.Success -> {
                Log.d(tag,"ViewModelDashboard --> : Success ${result.data.count()}")
                if(result.data.isEmpty) {
                    Log.d(tag," lista prestamos esta vacia")
                    _state.value = _state.value.copy(state = DashboardEvent.EMPTY)
                    return@launch
                }
                val loans = result.data.mapNotNull { document->
                    document.toObject<LoanDomain>()
                }

                if(preferences.isSuperAdmin){
                    _state.value = DashboardState.success(loans)
                    return@launch
                }

                //Include Title for the branch
                val localBranches = fromJson<List<Sucursales>>(preferences.branches)
                val newLoansWithTitles = localBranches.flatMap { branch ->
                    listOf(
                        LoanDomain(
                            type = TypePrestamo.TITLE.value,
                            title = branch.name
                        )
                    ) + loans.filter { it.branchId == branch.id }.distinct() // Elimina duplicados si los hay
                }
                _state.value = DashboardState.success(newLoansWithTitles)
            }
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
        _state.value = _state.value.copy(dialogState = DashboardDialogState.Loading)

        val result = repository.setLastPaymentForQuota(
            loanDomain.id ?: "",
            currentDate,
            quotesPendingNew,
            quotesPaidNew
        )

        when (result) {
            is PAResult.Error -> {
                Log.d(tag, "ViewModelRegister --> : Error ${result.exception.message}")
                _state.value = _state.value.copy(dialogState = DashboardDialogState.Error(result.exception.message ?: ""))
            }

            is PAResult.Success -> {
                Log.d(tag, "ViewModelRegister --> : Success ${result.data}")
                handledDetailPayment(loanDomain, currentDate, quotesPaidNew, quotesPendingNew, totalAmountToPay)
            }
        }
    }

    private fun handledDetailPayment(
        loanDomain: LoanDomain,
        currentDate: String,
        quotesPaidNew: Int,
        quotesPendingNew: Int,
        totalAmountToPay: Double,
    ) = viewModelScope.launch {
        val detail = DetallePrestamoSender(
            idPrestamo = loanDomain.id,
            fechaPago = currentDate,
            pagoTotal = totalAmountToPay,
            unixtime = getFechaActualNormalInUnixtime()
        )

        when (val resultDetail = repository.createDetail(detail)) {
            is PAResult.Error -> {
                Log.d(tag, "--> : Error ${resultDetail.exception.message}")
                _state.value = _state.value.copy(dialogState = DashboardDialogState.Error(resultDetail.exception.message ?: ""))
            }

            is PAResult.Success -> {
                //_message.value = "Se actualizo el pago"
                Log.d(tag, "--> : Success ${resultDetail.data}")

                //Here also update the loans list with the new data changed
                val idLoan = loanDomain.id ?: ""
                val currentLoan = _state.value.loans?.firstOrNull { it.id == idLoan }
                val newLoan = currentLoan?.copy(
                    lastPaymentDate = currentDate,
                    quotasPaid = quotesPaidNew,
                    quotasPending = quotesPendingNew
                )

                val newLoans = _state.value.loans?.mapNotNull { loan ->
                    if (loan.id == idLoan) { newLoan } else { loan }
                }

                _state.value = _state.value.copy(dialogState = DashboardDialogState.SuccessUpdateLoan, loans = newLoans)
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

        _state.value = _state.value.copy(dialogState = DashboardDialogState.Loading)
        val result = repository.setLastPayment(
            loanDomain.id ?: "",
            currentDate,
            quotesPendingNew,
            quotesPaidNew
        )
        when (result) {
            is PAResult.Error -> {
                Log.d(tag, "ViewModelRegister --> : Error ${result.exception.message}")
                _state.value = _state.value.copy(dialogState = DashboardDialogState.Error(result.exception.message ?: ""))
            }

            is PAResult.Success -> {
                Log.d(tag, "ViewModelRegister --> : Success ${result.data}")
                handledDetailPayment(loanDomain, currentDate, quotesPaidNew, quotesPendingNew, totalAmountToPay)
            }
        }
    }

    private fun closeLoan(id:String) = viewModelScope.launch {
        _state.value = _state.value.copy(dialogState = DashboardDialogState.Loading)
        val result = repository.closeLoan(id)
        when(result){
            is PAResult.Error -> {
                Log.d(tag,"ViewModelRegister --> : Error ${result.exception.message}")
                _state.value = _state.value.copy(dialogState = DashboardDialogState.ErrorCloseLoan)
            }
            is PAResult.Success -> {
                _state.value = _state.value.copy(dialogState = DashboardDialogState.SuccessCloseLoan)
            }
        }
    }

    private fun resetStatusDialogs() {
        _state.value = _state.value.copy(dialogState = DashboardDialogState.None)
    }


    data class DashboardState(
        val state : DashboardEvent,
        val loans: List<LoanDomain>? = null,
        val message: String? = null,
        val dialogState: DashboardDialogState = DashboardDialogState.None,
    ){
        companion object {
            fun idle() = DashboardState(DashboardEvent.LOADING)
            fun loading() = DashboardState(DashboardEvent.LOADING)
            fun success(loans: List<LoanDomain>) = DashboardState(DashboardEvent.SUCCESS, loans)
            fun error(message: String) = DashboardState(DashboardEvent.ERROR, message = message)
        }
    }

    sealed class DashboardIntent {
        data object LoadLoans : DashboardIntent()
        data class UpdateLoan(
            val loanDomain: LoanDomain,
            val quotesToPay: Int,
        ) : DashboardIntent()
        data class CloseLoan(val loanId: String) : DashboardIntent()
        data object ResetStatusDialogs : DashboardIntent()
        data class SendMessageToOtherApp(val loanDomain: LoanDomain, val context: Context) : DashboardIntent()
        data class SendMessageToWhatsApp(val loanDomain: LoanDomain, val context: Context) : DashboardIntent()
    }

    enum class DashboardEvent {
        LOADING,
        SUCCESS,
        ERROR,
        EMPTY
    }

    sealed class DashboardDialogState {
        data object None : DashboardDialogState() // No dialog is showing
        data object Loading : DashboardDialogState() // Loading dialog is active
        data class Error(val message: String) : DashboardDialogState() // Error dialog with a message
        data object SuccessCloseLoan : DashboardDialogState() // Success dialog for loan closure
        data object SuccessUpdateLoan : DashboardDialogState() // Success dialog for loan update
        data object ErrorCloseLoan : DashboardDialogState() // Error dialog for loan closure
    }
}