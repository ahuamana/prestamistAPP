package com.paparazziapps.pretamistapp.presentation.registro.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.LoanType
import com.paparazziapps.pretamistapp.helper.getDoubleWithOneDecimalsReturnDouble
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.data.repository.PARepository
import com.paparazziapps.pretamistapp.domain.PAConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class ViewModelRegister (
    private val repository: PARepository,
    val handle: SavedStateHandle
) : ViewModel(){

    private val tag = ViewModelRegister::class.java.simpleName

    //PAConstants.EXTRA_LOAN_JSON
    private val loanDomain = getLoanDomainFromExtras()

    var _message = MutableLiveData<String>()
    var _montoDiario = MutableLiveData<Double>()

    private val _dailyStringMode : MutableStateFlow<String> = MutableStateFlow(LoanType.DAILY.description)
    val dailyStringMode : StateFlow<String> = _dailyStringMode.asStateFlow()

    fun setDailyStringMode(value: String) {
        //split between spaces and get the first element handle the case of the string having a space at the end
        val description = value.trim().split(" ").firstOrNull()?:LoanType.DAILY.description
        _dailyStringMode.value = description
    }

    fun getMessage() :LiveData<String>{
        return  _message
    }

    fun getMontoDiario() : LiveData<Double> {
        return _montoDiario
    }

    private fun getLoanDomainFromExtras(): LoanDomain? {
        val extrasLoan = handle.get<String>(PAConstants.EXTRA_LOAN_JSON)
        Log.d(tag, "EXTRAS: $extrasLoan")
        return extrasLoan?.let {
            Gson().fromJson(it, LoanDomain::class.java)
        }
    }

    fun calcularMontoDiario(capital:Int, interes:Int, dias:Int) {
        try {
            val newCapital = capital.toDouble()
            val newInteres = interes.toDouble()

            val interesFinal = newCapital * (newInteres/100)
            val montodiario = (interesFinal + newCapital)/dias
            _montoDiario.value = getDoubleWithOneDecimalsReturnDouble(montodiario)?:-9999.00

        }catch (e:Exception) {
            Log.d(tag,"ERROR: ${e.message}")
        }
    }

    fun createPrestamo(loanDomain: LoanDomain, idSucursal:Int, onComplete: (Boolean, String, String?, Boolean) -> Unit)  = viewModelScope.launch {
        var isCorrect = false
        val result = repository.createLoan(loanDomain, idBranch = idSucursal)

        when (result) {
            is PAResult.Error -> {
                isCorrect = false
                _message.value = "La solicitud no se pudo procesar, intentalo otra vez"
                onComplete(
                    isCorrect,
                    "La solicitud no se pudo procesar, intentalo otra vez",
                    "",
                    false)
            }

            is PAResult.Success -> {
                _message.value = "El prestamo se registro correctamente"
                isCorrect = true
                onComplete(isCorrect, "El prestamo se registro correctamente", "", false)
            }
        }
    }

}