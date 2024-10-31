package com.paparazziapps.pretamistapp.modulos.registro.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.paparazziapps.pretamistapp.domain.LoanType
import com.paparazziapps.pretamistapp.helper.getDoubleWithOneDecimalsReturnDouble
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.data.providers.LoanProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.Exception

class ViewModelRegister (
    private val mLoanProvider: LoanProvider
) : ViewModel(){

    private val tag = ViewModelRegister::class.java.simpleName

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

    fun createPrestamo(loanDomain: LoanDomain, idSucursal:Int, onComplete: (Boolean, String, String?, Boolean) -> Unit) {
        var isCorrect = false
        try {
        mLoanProvider.create(loanDomain, idSucursal = idSucursal).addOnCompleteListener {
                if(it.isSuccessful)
                {
                    _message.value = "El prestamo se registro correctamente"
                    isCorrect = true
                    onComplete(isCorrect, "El prestamo se registro correctamente", "", false)
                }

                if (it.isCanceled)
                {
                    isCorrect = false
                    _message.value = "La solicitud de registro se canceló"
                    onComplete(isCorrect, "La solicitud de registro se canceló", "", false)
                }

        }.addOnFailureListener {

            var errorMessage = it.message.toString()

            _message.value = errorMessage

            isCorrect = false
            _message.value = "La solicitud no se pudo procesar, intentalo otra vez"
            onComplete(isCorrect, "La solicitud no se pudo procesar, intentalo otra vez", "", false)
        }

        }catch (t:Throwable) {
           var errorMessage = t.message.toString()
            println("Error : $errorMessage")

            _message.value = errorMessage

            isCorrect = false
            _message.value = "La solicitud no se proceso, contacte con soporte!"
            onComplete(isCorrect, "La solicitud no se proceso, contacte con soporte!", "", false)
        }
    }

}