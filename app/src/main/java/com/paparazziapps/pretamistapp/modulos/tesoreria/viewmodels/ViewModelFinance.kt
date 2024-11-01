package com.paparazziapps.pretamistapp.modulos.tesoreria.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObject
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.helper.*
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.data.providers.DetailLoanProvider
import com.paparazziapps.pretamistapp.data.providers.LoanProvider
import com.paparazziapps.pretamistapp.domain.DetallePrestamoSender
import kotlinx.coroutines.launch

class ViewModelFinance (
    private val loanProvider: LoanProvider,
    private val detailLoanProvider: DetailLoanProvider
) : ViewModel() {

    var  _message = MutableLiveData<String>()

    var _prestamos = MutableLiveData<MutableList<LoanDomain>>()
    var _pagosTotalesByTime = MutableLiveData<Double>()

    private val tag = ViewModelFinance::class.java.simpleName

    fun getMessage() : LiveData<String> =  _message
    fun receivePrestamos (): LiveData<MutableList<LoanDomain>> =_prestamos
    fun getPagosTotalesByTime():LiveData<Double> = _pagosTotalesByTime

    fun getPrestamosSize(onComplete: (Boolean, String, Int?, Boolean) -> Unit) = viewModelScope.launch {
        var isCorrect = false
        val listLoanRespons = mutableListOf<LoanDomain>()
        val result = loanProvider.getLoans()

        when(result){
            is PAResult.Error -> {
                isCorrect = false
                onComplete(isCorrect, "No se pudo obtener los prestamos, porfavor comuníquese con soporte!", null, false)
            }
            is PAResult.Success -> {
                if(result.data.isEmpty) {
                    isCorrect = true
                    onComplete(isCorrect,"",0,false)
                }else {
                    result.data.forEach { document->
                        listLoanRespons.add(document.toObject<LoanDomain>())
                    }
                    _prestamos.value = listLoanRespons
                    onComplete(isCorrect,"",result.data.size(),false)
                }
            }
        }
    }

    fun getPrestamosByTime(timeStart:Long, timeEnd:Long, idSucursal:Int) = viewModelScope.launch {
        var pagosTotalesXfecha = 0.0
        val result = detailLoanProvider.getLoanByDate(timeStart, timeEnd.plus(DiaUnixtime), idSucursal)

        when(result) {
            is PAResult.Error -> {
                _pagosTotalesByTime.value = 0.0
            }
            is PAResult.Success -> {
                if(result.data.isEmpty) {
                    _pagosTotalesByTime.value = 0.0
                }else {
                    result.data.forEach { document->
                        val dps = document.toObject<DetallePrestamoSender>()
                        pagosTotalesXfecha += dps.pagoTotal?:0.0
                    }
                    pagosTotalesXfecha = getDoubleWithTwoDecimalsReturnDouble(pagosTotalesXfecha)?:0.0
                    _pagosTotalesByTime.value = pagosTotalesXfecha
                }
            }
        }
    }


    fun getPagosHoy(onComplete: (Boolean, String, Double?, Boolean) -> Unit) = viewModelScope.launch {
        var isCorrect = false
        var pagosTotalesHoy = 0.0
        val result = detailLoanProvider.getDetailLoanByDate(getFechaActualNormalCalendar())

        when (result) {
            is PAResult.Error -> {
                Log.d(tag, "Error: ${result.exception.message}")
                isCorrect = false
                onComplete(
                    isCorrect,
                    "No se pudo obtener los pagos de hoy, porfavor comuníquese con soporte!",
                    null,
                    false
                )
            }

            is PAResult.Success -> {
                if (result.data.isEmpty) {
                    isCorrect = true
                    onComplete(isCorrect, "", 0.0, false)
                } else {
                    result.data.forEach { document ->
                        val dps = document.toObject<DetallePrestamoSender>()
                        pagosTotalesHoy += dps.pagoTotal ?: 0.0
                    }
                    pagosTotalesHoy = getDoubleWithTwoDecimalsReturnDouble(pagosTotalesHoy) ?: 0.0
                    onComplete(isCorrect, "", pagosTotalesHoy, false)
                }
            }
        }
    }



    fun getPagosAyer(onComplete: (Boolean, String, Double?, Boolean) -> Unit) = viewModelScope.launch {
        var isCorrect = false
        var pagosTotalesAyer = 0.0
        val details = detailLoanProvider.getDetailLoanByDate(getYesterdayFechaNormal())

        when(details){
            is PAResult.Error -> {
                Log.d(tag, "Error: ${details.exception.message}")
                isCorrect = false
                onComplete(isCorrect,
                    "No se pudo obtener los pagos de ayer, porfavor comuníquese con soporte!",
                    null, false)
            }
            is PAResult.Success -> {
                if (details.data.isEmpty) {
                    isCorrect = true
                    onComplete(isCorrect, "", 0.0, false)
                } else {
                    details.data.forEach { document ->
                        val dps = document.toObject<DetallePrestamoSender>()
                        pagosTotalesAyer += dps.pagoTotal ?: 0.0
                    }
                    pagosTotalesAyer = getDoubleWithTwoDecimalsReturnDouble(pagosTotalesAyer) ?: 0.0
                    onComplete(isCorrect, "", pagosTotalesAyer, false)
                }
            }
        }
    }

}