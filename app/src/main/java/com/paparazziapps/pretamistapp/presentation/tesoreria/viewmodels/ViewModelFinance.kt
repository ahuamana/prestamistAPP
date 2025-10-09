package com.paparazziapps.pretamistapp.presentation.tesoreria.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObject
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.helper.*
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.data.repository.PARepository
import com.paparazziapps.pretamistapp.domain.DetailLoanForm
import com.paparazziapps.pretamistapp.domain.DetailLoanResponse
import com.paparazziapps.pretamistapp.domain.toDetailLoanDomain
import kotlinx.coroutines.launch

class ViewModelFinance (
    private val repository: PARepository
) : ViewModel() {

    var _prestamos = MutableLiveData<MutableList<LoanDomain>>()
    var _pagosTotalesByTime = MutableLiveData<Double>()

    private val tag = ViewModelFinance::class.java.simpleName

    fun receivePrestamos (): LiveData<MutableList<LoanDomain>> =_prestamos
    fun getPagosTotalesByTime():LiveData<Double> = _pagosTotalesByTime

    fun getLoansSize(onComplete: (Boolean, String, Int?, Boolean) -> Unit) = viewModelScope.launch {
        var isCorrect = false
        val listLoanRespons = mutableListOf<LoanDomain>()
        val result = repository.getLoans()

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
                    onComplete(true,"",result.data.size(),false)
                }
            }
        }
    }

    fun getLoansByTime(timeStart:Long, timeEnd:Long, idSucursal:Int) = viewModelScope.launch {
        var pagosTotalesXfecha = 0.0
        val result = repository.getLoanByDate(timeStart, timeEnd.plus(DiaUnixtime), idSucursal)
        Log.d(tag, "getLoansByTime: ${result.toString()}")

        when(result) {
            is PAResult.Error -> {
                _pagosTotalesByTime.value = 0.0
            }
            is PAResult.Success -> {
                if(result.data.isEmpty) {
                    _pagosTotalesByTime.value = 0.0
                }else {
                    result.data.forEach { document->
                        val dps = document.toObject<DetailLoanForm>()
                        pagosTotalesXfecha += dps.totalAmountToPay?:0.0
                    }
                    pagosTotalesXfecha = getDoubleWithTwoDecimalsReturnDouble(pagosTotalesXfecha)?:0.0
                    _pagosTotalesByTime.value = pagosTotalesXfecha
                }
            }
        }
    }


    fun getPaymentsToday(onComplete: (Boolean, String, Double?, Boolean) -> Unit) = viewModelScope.launch {
        var isCorrect = false
        var pagosTotalesHoy = 0.0
        val result = repository.getDetailLoanByDate(getFechaActualNormalCalendar())

        Log.d(tag, "getPaymentsToday result: $result")

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
                    isCorrect = true
                    Log.d(tag, "getPaymentsToday data: ${result.data}")
                    result.data.forEach { document ->
                        val dps = document.toObject<DetailLoanResponse>().toDetailLoanDomain()
                        Log.d(tag, "getPaymentsToday totalAmountToPay: ${dps.totalAmountToPay}")
                        pagosTotalesHoy += dps.totalAmountToPay
                    }
                    Log.d(tag, "getPaymentsToday pagosTotalesHoy: $pagosTotalesHoy")
                    pagosTotalesHoy = getDoubleWithTwoDecimalsReturnDouble(pagosTotalesHoy)
                    onComplete(isCorrect, "", pagosTotalesHoy, false)
                }
            }
        }
    }



    fun getPaymentsYesterday(onComplete: (Boolean, String, Double?, Boolean) -> Unit) = viewModelScope.launch {
        var isCorrect = false
        var pagosTotalesAyer = 0.0
        val details = repository.getDetailLoanByDate(getYesterdayFechaNormal())

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
                    isCorrect = true
                    details.data.forEach { document ->
                        val dps = document.toObject<DetailLoanResponse>().toDetailLoanDomain()
                        pagosTotalesAyer += dps.totalAmountToPay
                    }
                    pagosTotalesAyer = getDoubleWithTwoDecimalsReturnDouble(pagosTotalesAyer) ?: 0.0
                    onComplete(isCorrect, "", pagosTotalesAyer, false)
                }
            }
        }
    }

}