package com.paparazziapps.pretamistapp.modulos.tesoreria.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.common.internal.Constants
import com.google.firebase.firestore.ktx.toObject
import com.paparazziapps.pretamistapp.helper.*
import com.paparazziapps.pretamistapp.modulos.registro.pojo.Prestamo
import com.paparazziapps.pretamistapp.modulos.registro.providers.DetallePrestamoProvider
import com.paparazziapps.pretamistapp.modulos.registro.providers.PrestamoProvider
import com.paparazziapps.pretamistapp.modulos.tesoreria.pojo.DetallePrestamoSender

class ViewModelTesoreria private constructor() {

    var  _message = MutableLiveData<String>()
    val mPrestamoProvider = PrestamoProvider()
    val mDetallePrestamo = DetallePrestamoProvider()

    var _prestamos = MutableLiveData<MutableList<Prestamo>>()

    fun getMessage() : LiveData<String> {
        return  _message
    }

    fun receivePrestamos (): LiveData<MutableList<Prestamo>>
    {
        return _prestamos
    }

    fun getPrestamosSize(onComplete: (Boolean, String, Int?, Boolean) -> Unit)
    {
        var isCorrect = false
        var listPrestamos = mutableListOf<Prestamo>()

        try {

            mPrestamoProvider.getPrestamos().addOnSuccessListener {


                isCorrect = true
                if(it.isEmpty)
                {
                    onComplete(isCorrect,"",0,false)
                }else
                {
                    it.forEach { document->
                        listPrestamos.add(document.toObject<Prestamo>())
                    }
                    _prestamos.value = listPrestamos
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
            onComplete(isCorrect, "No se pudo obtener los pagos de hoy, porfavor comuníquese con soporte!", null, false)

        }
    }

    fun getPagosHoy(onComplete: (Boolean, String, Double?, Boolean) -> Unit)
    {
        var isCorrect = false
        var pagosTotalesHoy = 0.0

        try {

            mDetallePrestamo.getDetallePrestamosByFecha(getFechaActualNormalCalendar()).addOnSuccessListener {

                it.forEach { document ->
                    //println("Documento Pagos MVVM--> $document")
                    var dps =document.toObject<DetallePrestamoSender>()
                    pagosTotalesHoy += dps.pagoTotal?:0.0
                    //println("Pago total hoy: $pagosTotalesHoy")
                }

                isCorrect = true
                pagosTotalesHoy= getDoubleWithTwoDecimalsReturnDouble(pagosTotalesHoy)!!
                onComplete(isCorrect,"",pagosTotalesHoy,false)

            }.addOnFailureListener {

                println("Error: ${it.message}")
                isCorrect = false
                onComplete(isCorrect, "No se pudo obtener los pagos de hoy, porfavor comuníquese con soporte!", null, false)

        }



        }catch (t:Throwable)
        {

            println("Error throwable: ${t.message}")
            isCorrect = false
            onComplete(isCorrect, "No se pudo obtener los pagos de hoy, porfavor comuníquese con soporte!", null, false)
        }


    }



    fun getPagosAyer(onComplete: (Boolean, String, Double?, Boolean) -> Unit)
    {
        var isCorrect = false
        var pagosTotalesAyer = 0.0

        try {

            mDetallePrestamo.getDetallePrestamosByFecha(getYesterdayFechaNormal()).addOnSuccessListener {

                it.forEach { document ->
                    println("Documento Pagos MVVM--> $document")
                    var dps =document.toObject<DetallePrestamoSender>()
                    pagosTotalesAyer += dps.pagoTotal?:0.0
                    println("Pago total ayer: $pagosTotalesAyer")
                }

                isCorrect = true
                pagosTotalesAyer= getDoubleWithTwoDecimalsReturnDouble(pagosTotalesAyer)!!
                onComplete(isCorrect,"",pagosTotalesAyer,false)

            }.addOnFailureListener {

                println("Error: ${it.message}")
                isCorrect = false
                onComplete(isCorrect, "No se pudo obtener los pagos de ayer, porfavor comuníquese con soporte!", null, false)

            }



        }catch (t:Throwable)
        {

            println("Error throwable: ${t.message}")
            isCorrect = false
            onComplete(isCorrect, "No se pudo obtener los pagos de ayer, porfavor comuníquese con soporte!", null, false)
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