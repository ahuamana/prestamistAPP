package com.paparazziapps.pretamistapp.modulos.registro.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.lang.Exception

open class ViewModelRegister private constructor(){

    var _message = MutableLiveData<String>()
    var _montoDiario = MutableLiveData<Double>()


    fun getMessage() :LiveData<String>{
        return  _message
    }

    fun getMontoDiario() : LiveData<Double>
    {
        return _montoDiario
    }

    fun calcularMontoDiario(capital:Double, interes:Double, dias:Int)
    {
        try {
            var interesNuevo =capital * (interes/100)
            var montodiario =(interesNuevo + capital)/dias
            _montoDiario.value = String.format("%.1f", montodiario).toDouble()

        }catch (e:Exception)
        {
            println("ERROR: ${e.message}")
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