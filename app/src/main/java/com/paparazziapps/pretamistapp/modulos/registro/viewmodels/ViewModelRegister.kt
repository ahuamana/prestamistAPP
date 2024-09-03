package com.paparazziapps.pretamistapp.modulos.registro.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.paparazziapps.pretamistapp.helper.getDoubleWithOneDecimalsReturnDouble
import com.paparazziapps.pretamistapp.modulos.registro.pojo.Prestamo
import com.paparazziapps.pretamistapp.modulos.registro.providers.PrestamoProvider
import java.lang.Exception

open class ViewModelRegister private constructor(){

    var _message = MutableLiveData<String>()
    var _montoDiario = MutableLiveData<Double>()
    var mPrestamoProvider = PrestamoProvider()

    fun getMessage() :LiveData<String>{
        return  _message
    }

    fun getMontoDiario() : LiveData<Double>
    {
        return _montoDiario
    }

    fun calcularMontoDiario(capital:Int, interes:Int, dias:Int)
    {
        try {
            var newCapital = capital.toDouble()
            var newInteres = interes.toDouble()

            var interesFinal = newCapital * (newInteres/100)
            var montodiario = (interesFinal + newCapital)/dias
            _montoDiario.value = getDoubleWithOneDecimalsReturnDouble(montodiario)?:-9999.00

        }catch (e:Exception)
        {
            println("ERROR: ${e.message}")
        }
    }

    fun createPrestamo(prestamo: Prestamo, idSucursal:Int, onComplete: (Boolean, String, String?, Boolean) -> Unit)
    {
        var isCorrect = false
        try {

        mPrestamoProvider.create(prestamo, idSucursal = idSucursal).addOnCompleteListener {
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

        }catch (t:Throwable)
        {
           var errorMessage = t.message.toString()
            println("Error : $errorMessage")

            _message.value = errorMessage

            isCorrect = false
            _message.value = "La solicitud no se proceso, contacte con soporte!"
            onComplete(isCorrect, "La solicitud no se proceso, contacte con soporte!", "", false)
        }
    }







    companion object Singleton{
        private var instance:ViewModelRegister? = null

        fun getInstance():ViewModelRegister =
            instance ?: ViewModelRegister(
                //local y remoto
            ).also { instance = it }

        fun destroyInstance(){
            instance = null
        }
    }

}