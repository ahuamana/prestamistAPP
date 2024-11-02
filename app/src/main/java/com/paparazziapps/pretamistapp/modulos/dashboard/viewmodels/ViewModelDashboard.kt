package com.paparazziapps.pretamistapp.modulos.dashboard.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObject
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.helper.INT_DEFAULT
import com.paparazziapps.pretamistapp.helper.getFechaActualNormalInUnixtime
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.domain.PaymentScheduled
import com.paparazziapps.pretamistapp.domain.PaymentScheduledEnum
import com.paparazziapps.pretamistapp.data.providers.DetailLoanProvider
import com.paparazziapps.pretamistapp.data.providers.LoanProvider
import com.paparazziapps.pretamistapp.domain.DetallePrestamoSender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ViewModelDashboard (
    private val loanProvider: LoanProvider,
    private val detailLoanProvider: DetailLoanProvider
) : ViewModel(){

    private val tag = ViewModelDashboard::class.java.simpleName
    private var _loans = MutableStateFlow<MutableList<LoanDomain>>(mutableListOf())
    val loans: StateFlow<MutableList<LoanDomain>> = _loans

    fun getLoans() = viewModelScope.launch {
        val loans = mutableListOf<LoanDomain>()

        val result = loanProvider.getLoans()

        when(result){
            is PAResult.Error -> TODO()
            is PAResult.Success -> {

                if(result.data.isEmpty) {
                    Log.d(tag," lista prestamos esta vacia")
                    return@launch
                }
                result.data.forEach { document->
                    loans.add(document.toObject())
                    Log.d(tag," lista prestamos ${loans.size}")
                }
                _loans.value = loans
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

                    val result = loanProvider.setLastPayment(id?:"",fecha?:"",diasRestantesPorPagar,diasPagadosNuevo)

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

                    val result =  loanProvider.setLastPaymentForQuota(
                    id?:"",
                    fecha?:"",
                    diasRestantesPorPagar,
                    paidDays = newCurrentPaidDays,
                    quotesPaid = diasPagadosNuevo
                    )

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

                            val resultDetail = detailLoanProvider.createDetail(detalle)

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




    fun cerrarPrestamo(id:String?, onComplete: (Boolean, String, String?, Boolean) -> Unit) = viewModelScope.launch {
        var isCorrect = false
        val result = loanProvider.closeLoan(id?:"")

        when(result){
            is PAResult.Error -> {
                Log.d(tag,"ViewModelRegister --> : Error ${result.exception.message}")
                //_message.value = "No se pudo actualizar el pago, intentelo otra vez"
                isCorrect = false
                onComplete(isCorrect, "No se pudo cerrar el pago, inténtelo otra vez", null, false)
            }
            is PAResult.Success -> {
                isCorrect = true
                onComplete(isCorrect, "Se cerro el pago", null, false)
            }
        }
    }
}