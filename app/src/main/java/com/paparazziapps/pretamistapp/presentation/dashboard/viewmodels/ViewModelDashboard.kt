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

    fun handleIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.LoadLoans -> loadLoans()
            is DashboardIntent.UpdateLoan -> updateLoan(
                intent.loanDomain,
                intent.totalAmountToPay,
                intent.adapterPosition,
                intent.daysMissingToPay,
                intent.paidDays,
                intent.isClosed
            )
            is DashboardIntent.CloseLoan -> closeLoan(intent.loanId)
            DashboardIntent.UpdateStatusDialogs -> {
                resetStatusDialogs()
            }
        }
    }

    private fun updateLoan(
        loanDomain: LoanDomain,
        totalAmountToPay: Double,
        adapterPosition: Int,
        daysMissingToPay: Int,
        paidDays: Int,
        closed: Boolean
    ) {
        TODO("Not yet implemented")
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
                    ) + loans.filter { it.sucursalId == branch.id }.distinct() // Elimina duplicados si los hay
                }
                _state.value = DashboardState.success(newLoansWithTitles)
            }
        }
    }

    fun updateUltimoPago(loanDomain: LoanDomain,
                         id:String?, fecha:String?,
                         pagoTotal:Double, diasRestantesPorPagar:Int,
                         diasPagadosNuevo:Int,
                         onComplete: (Boolean, String, String?, Boolean) -> Unit) = viewModelScope.launch {
        var isCorrect:Boolean
        try {
            val typeLoan = PaymentScheduled.getPaymentScheduledById(loanDomain.typeLoan?: INT_DEFAULT)
            when(typeLoan) {
                PaymentScheduledEnum.DAILY -> {
                    //use fun setLastPayment

                    val result = repository.setLastPayment(id?:"",fecha?:"",diasRestantesPorPagar,diasPagadosNuevo)

                    when(result){
                        is PAResult.Error -> {
                            Log.d(tag,"ViewModelRegister --> : Error ${result.exception.message}")
                            //_message.value = "No se pudo actualizar el pago, intentelo otra vez"
                            isCorrect = false
                            onComplete(isCorrect, "No se pudo actualizar el pago, inténtelo otra vez", null, false)
                        }
                        is PAResult.Success -> {
                            //_message.value = "Se actualizo el pago"
                            isCorrect = true
                            onComplete(isCorrect, "Se actualizo el pago", null, false)
                        }
                    }
                }
                else ->{
                    //diasPagados * the type loan days times the quotas
                    val paidDaysBefore = loanDomain.diasPagados?:0
                    val currentLoanDays = PaymentScheduled.getPaymentScheduledById(loanDomain.typeLoan?: INT_DEFAULT).days
                    val newCurrentPaidDays = paidDaysBefore + (currentLoanDays.times(diasPagadosNuevo))

                    val result =  repository.setLastPaymentForQuota(
                    id?:"",
                    fecha?:"",
                    diasRestantesPorPagar,
                    paidDays = newCurrentPaidDays,
                    quotesPaid = diasPagadosNuevo)

                    when(result){
                        is PAResult.Error -> {
                            Log.d(tag,"ViewModelRegister --> : Error ${result.exception.message}")
                            //_message.value = "No se pudo actualizar el pago, intentelo otra vez"
                            isCorrect = false
                            onComplete(isCorrect, "No se pudo actualizar el pago, inténtelo otra vez", null, false)
                        }
                        is PAResult.Success -> {
                            val detalle = DetallePrestamoSender(
                                idPrestamo = id,
                                fechaPago = fecha,
                                pagoTotal = pagoTotal,
                                unixtime = getFechaActualNormalInUnixtime())

                            val resultDetail = repository.createDetail(detalle)

                            when(resultDetail){
                                is PAResult.Error -> {
                                    Log.d(tag,"ViewModelRegister --> : Error ${resultDetail.exception.message}")
                                    //_message.value = "No se pudo actualizar el pago, intentelo otra vez"
                                    isCorrect = false
                                    onComplete(isCorrect, "No se pudo crear el ultimo pago, inténtelo otra vez", null, false)
                                }
                                is PAResult.Success -> {
                                    //_message.value = "Se actualizo el pago"
                                    Log.d(tag,"ViewModelRegister --> : Success ${resultDetail.data}")
                                    isCorrect = true
                                    onComplete(isCorrect, "Se actualizo el pago", null, false)
                                }
                            }
                        }
                    }
                }
            }


        }catch (t:Throwable) {
            isCorrect = false
            Log.d(tag,"Error throable model ----> ${t.message} -- ${t.cause}")
            onComplete(isCorrect, "No se pudo actualizar el pago, porfavor comuníquese con soporte!", null, false)

        }
    }

    private fun closeLoan(id:String) = viewModelScope.launch {
        _state.value = _state.value.copy(showLoadingDialog = true)
        val result = repository.closeLoan(id)
        when(result){
            is PAResult.Error -> {
                Log.d(tag,"ViewModelRegister --> : Error ${result.exception.message}")
                _state.value = _state.value.copy(showDialogErrorCloseLoan = true, showLoadingDialog = false)
            }
            is PAResult.Success -> {
                _state.value = _state.value.copy(showDialogSuccessCloseLoan = true, showLoadingDialog = false)
            }
        }
    }

    fun resetStatusDialogs() {
        _state.value = _state.value.copy(
            showDialogErrorCloseLoan = false,
            showDialogSuccessCloseLoan = false,
            showLoadingDialog = false
        )
    }

    data class DashboardState(
        val state : DashboardEvent,
        val loans: List<LoanDomain>? = null,
        val message: String? = null,
        val showLoadingDialog: Boolean = false,
        val showDialogErrorCloseLoan: Boolean = false,
        val showDialogSuccessCloseLoan: Boolean = false
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
            val totalAmountToPay: Double,
            val adapterPosition: Int,
            val daysMissingToPay: Int,
            val paidDays: Int,
            val isClosed: Boolean
        ) : DashboardIntent()
        data class CloseLoan(val loanId: String) : DashboardIntent()
        data object UpdateStatusDialogs : DashboardIntent()
    }

    enum class DashboardEvent {
        LOADING,
        SUCCESS,
        ERROR,
        EMPTY
    }
}