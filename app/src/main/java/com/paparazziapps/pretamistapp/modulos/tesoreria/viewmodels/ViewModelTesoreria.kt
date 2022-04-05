package com.paparazziapps.pretamistapp.modulos.tesoreria.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.paparazziapps.pretamistapp.modulos.registro.providers.PrestamoProvider

class ViewModelTesoreria private constructor() {

    var  _message = MutableLiveData<String>()
    val mPrestamoProvider = PrestamoProvider()

    fun getMessage() : LiveData<String> {
        return  _message
    }

    fun getPrestamosSize(onComplete: (Boolean, String, Int?, Boolean) -> Unit)
    {
        var isCorrect = false

        try {

            mPrestamoProvider.getPrestamos().addOnSuccessListener {

                isCorrect = true
                if(it.isEmpty)
                {

                    onComplete(isCorrect,"",0,false)
                }else
                {
                    onComplete(isCorrect,"",it.size(),false)
                }


            }.addOnFailureListener {
                println("Error: ${it.message}")
                isCorrect = false
                onComplete(isCorrect, "No se pudo obtener los prestamos, porfavor comuníquese con soporte!", null, false)

            }

        }catch (t:Throwable)
        {
            println("Error throwable: ${t.message}")
            isCorrect = false
            onComplete(isCorrect, "No se pudo obtener los prestamos, porfavor comuníquese con soporte!", null, false)

        }
    }

    companion object Singleton{
        private var instance:ViewModelTesoreria? = null

        fun getInstance():ViewModelTesoreria =
            instance ?: ViewModelTesoreria(

            ).also { instance = it }

        fun destroyInstance(){
            instance = null
        }
    }


}