package com.paparazziapps.pretamistapp.modulos.dashboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.toObject
import com.paparazziapps.pretamistapp.modulos.registro.pojo.Prestamo
import com.paparazziapps.pretamistapp.modulos.registro.providers.DetallePrestamoProvider
import com.paparazziapps.pretamistapp.modulos.registro.providers.PrestamoProvider
import com.paparazziapps.pretamistapp.modulos.tesoreria.pojo.DetallePrestamoSender

class ViewModelDashboard private constructor(){

    var _message = MutableLiveData<String>()
    var mPrestamoProvider = PrestamoProvider()
    var mDetallePrestamoProvider = DetallePrestamoProvider()

    var _prestamos = MutableLiveData<MutableList<Prestamo>>()

    fun getMessage() : LiveData<String> {
        return  _message
    }

    fun receivePrestamos (): LiveData<MutableList<Prestamo>>
    {
        return _prestamos
    }

    fun getPrestamos()
    {
        try {

            var listPrestamos = mutableListOf<Prestamo>()

            mPrestamoProvider.getPrestamos().addOnSuccessListener { prestamosFirebase ->

                if(prestamosFirebase.isEmpty)
                {
                    println(" lista prestamos esta vacia")
                }
                prestamosFirebase.forEach { document->
                    listPrestamos.add(document.toObject<Prestamo>())
                    println(" lista prestamos ${listPrestamos.size}")
                }

                println("ViewModel --->_Prestamos: ${listPrestamos.size}")
                _prestamos.value = listPrestamos
            }

        }catch (t:Throwable)
        {
            println("Error: ${t.message}")
        }

    }

    fun updateUltimoPago(id:String?, fecha:String?, pagoTotal:Double,diasRestantesPorPagar:Int, diasPagadosNuevo:Int,onComplete: (Boolean, String, String?, Boolean) -> Unit)
    {
        var isCorrect = false

        try {

            mPrestamoProvider.setLastPayment(id?:"",fecha?:"",diasRestantesPorPagar,diasPagadosNuevo).addOnCompleteListener {
                if(it.isSuccessful)
                {
                    var detalle = DetallePrestamoSender(
                        idPrestamo = id,
                        fechaPago = fecha,
                        pagoTotal = pagoTotal
                    )

                    mDetallePrestamoProvider.createDetalle(detalle).addOnCompleteListener {
                        if(it.isSuccessful)
                        {
                            //_message.value = "Se actualizo el pago"
                            isCorrect = true
                            onComplete(isCorrect, "Se actualizo el pago", null, false)
                        }else
                        {
                            println("Error: ${it.exception?.message}")
                            isCorrect = false
                            onComplete(isCorrect, "No se pudo crear el ultimo pago, inténtelo otra vez", null, false)
                        }
                    }.addOnFailureListener {
                        println("Error: ${it.message}")
                        isCorrect = false
                        onComplete(isCorrect, "No se pudo crear el ultimo pago, porfavor comuníquese con soporte!", null, false)
                    }

                }else
                {
                    println("ViewModelRegister --> : Error ${it.exception?.message}")
                    //_message.value = "No se pudo actualizar el pago, intentelo otra vez"
                    isCorrect = false
                    onComplete(isCorrect, "No se pudo actualizar el pago, inténtelo otra vez", null, false)
                }
            }


        }catch (t:Throwable)
        {
            isCorrect = false
            onComplete(isCorrect, "No se pudo actualizar el pago, porfavor comuníquese con soporte!", null, false)

            println("Error throable model ----> ${t.message}")
        }
    }

    fun cerrarPrestamo(id:String?, onComplete: (Boolean, String, String?, Boolean) -> Unit)
    {
        var isCorrect = false

        try {

            mPrestamoProvider.cerrarPrestamo(id?:"").addOnCompleteListener {
                if(it.isSuccessful)
                {
                    isCorrect = true
                    onComplete(isCorrect, "Se cerro el pago", null, false)

                }else
                {
                    println("ViewModelRegister --> : Error ${it.exception?.message}")
                    //_message.value = "No se pudo actualizar el pago, intentelo otra vez"
                    isCorrect = false
                    onComplete(isCorrect, "No se pudo cerrar el pago, inténtelo otra vez", null, false)
                }
            }


        }catch (t:Throwable)
        {
            isCorrect = false
            onComplete(isCorrect, "No se pudo cerrar el pago, porfavor comuníquese con soporte!", null, false)

            println("Error throable model ----> ${t.message}")
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