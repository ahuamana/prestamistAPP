package com.paparazziapps.pretamistapp.presentation.dashboard.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObject
import com.paparazziapps.pretamistapp.application.MyPreferences
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.helper.INT_DEFAULT
import com.paparazziapps.pretamistapp.helper.getFechaActualNormalInUnixtime
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.domain.PaymentScheduled
import com.paparazziapps.pretamistapp.domain.PaymentScheduledEnum
import com.paparazziapps.pretamistapp.data.repository.PARepository
import com.paparazziapps.pretamistapp.domain.DetallePrestamoSender
import com.paparazziapps.pretamistapp.domain.Sucursales
import com.paparazziapps.pretamistapp.domain.TypePrestamo
import com.paparazziapps.pretamistapp.helper.fromJson
import com.paparazziapps.pretamistapp.helper.getFechaActualNormalCalendar
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
        }
    }

    private fun updateLoan(
        loanDomain: LoanDomain,
        quotesToPay: Int,
        currentDate: String,
    ) {
        //Daily payment
        val idLoan = loanDomain.id ?: "" //Works for daily and other
        val needToClose = loanDomain.quotasPending == quotesToPay //Works for daily and other
        val totalAmountToPay = quotesToPay.times(loanDomain.amountPerQuota ?: 0.0) // rename to quotaAmount // works for daily and other

        //Other payment
        val quotesPaidBefore = loanDomain.quotasPaid ?: 0
        val quotesPaidNew = quotesPaidBefore + quotesToPay

        val quotesPendingBefore = loanDomain.quotasPending ?: 0
        val quotesPendingNew = quotesPendingBefore - quotesToPay

        val daysFromTypeLoan = PaymentScheduled.getPaymentScheduledById(loanDomain.typeLoan ?: INT_DEFAULT).days
        val quotesPaidInDays = quotesPaidNew * daysFromTypeLoan

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