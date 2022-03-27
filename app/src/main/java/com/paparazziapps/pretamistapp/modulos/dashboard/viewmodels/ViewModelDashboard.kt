package com.paparazziapps.pretamistapp.modulos.dashboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.toObject
import com.paparazziapps.pretamistapp.helper.MainApplication
import com.paparazziapps.pretamistapp.helper.MainApplication.Companion.ctx
import com.paparazziapps.pretamistapp.modulos.registro.pojo.Prestamo
import com.paparazziapps.pretamistapp.modulos.registro.providers.PrestamoProvider
import com.paparazziapps.pretamistapp.modulos.registro.viewmodels.ViewModelRegister

class ViewModelDashboard private constructor(){

    var _message = MutableLiveData<String>()
    var mPrestamoProvider = PrestamoProvider()

    var _prestamos = MutableLiveData<List<Prestamo>>()

    fun getMessage() : LiveData<String> {
        return  _message
    }

    fun receivePrestamos (): LiveData<List<Prestamo>>
    {
        return _prestamos
    }

    fun getPrestamos()
    {

        try {

            var listPrestamos = mutableListOf<Prestamo>()

            mPrestamoProvider.getPrestamos().addOnSuccessListener { prestamosFirebase ->

                prestamosFirebase.forEach { document->
                    listPrestamos.add(document.toObject<Prestamo>())
                    println(" lista prestamos ${listPrestamos.size}")
                }

                println("ViewModel --->_Prestamos: ${listPrestamos.size}")
                _prestamos.value = listPrestamos
            }

        }catch (t:Throwable)
        {



        }

    }


    //constructor
    companion object Singleton{
        private var instance: ViewModelDashboard? = null

        fun getInstance(): ViewModelDashboard =
            instance ?: ViewModelDashboard(
                //local y remoto
            ).also { instance = it }

        fun destroyInstance(){
            instance = null
        }
    }
}