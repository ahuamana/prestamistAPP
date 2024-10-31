package com.paparazziapps.pretamistapp.modulos.tesoreria.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.toObject
import com.paparazziapps.pretamistapp.helper.*
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.data.providers.DetailLoanProvider
import com.paparazziapps.pretamistapp.data.providers.LoanProvider
import com.paparazziapps.pretamistapp.domain.DetallePrestamoSender

class ViewModelFinance (
    private val loanProvider: LoanProvider,
    private val detailLoanProvider: DetailLoanProvider
) : ViewModel() {

    var  _message = MutableLiveData<String>()

    var _prestamos = MutableLiveData<MutableList<LoanDomain>>()
    var _pagosTotalesByTime = MutableLiveData<Double>()

    fun getMessage() : LiveData<String> =  _message
    fun receivePrestamos (): LiveData<MutableList<LoanDomain>> =_prestamos
    fun getPagosTotalesByTime():LiveData<Double> = _pagosTotalesByTime

    fun getPrestamosSize(onComplete: (Boolean, String, Int?, Boolean) -> Unit)
    {
        var isCorrect = false
        var listLoanRespons = mutableListOf<LoanDomain>()

        try {

            loanProvider.getLoans().addOnSuccessListener {


                isCorrect = true
                if(it.isEmpty)
                {
                    onComplete(isCorrect,"",0,false)
                }else
                {
                    it.forEach { document->
                        listLoanRespons.add(document.toObject<LoanDomain>())
                    }
                    _prestamos.value = listLoanRespons
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

    fun getPrestamosByTime(timeStart:Long, timeEnd:Long, idSucursal:Int)
    {
        var pagosTotalesXfecha = 0.0

        try {
            detailLoanProvider.getLoanByDate(timeStart, timeEnd.plus(DiaUnixtime), idSucursal).addOnSuccessListener {
                if(it.isEmpty)
                {
                    println("Fechas Vacias")
                    _pagosTotalesByTime.value = 0.0
                }else
                {
                    it.forEach { document->
                        var dps = document.toObject<DetallePrestamoSender>()
                        println("Pago total de item: ${dps.pagoTotal}")
                        pagosTotalesXfecha += dps.pagoTotal?:0.0
                    }
                    println("Total caja: $pagosTotalesXfecha")
                    pagosTotalesXfecha = getDoubleWithTwoDecimalsReturnDouble(pagosTotalesXfecha)?:0.0
                    _pagosTotalesByTime.value = pagosTotalesXfecha
                }

            }.addOnFailureListener {
                println("Error FailureListener: ${it.message}")
            }

        }catch (t:Throwable) {
            println("Error throwable: ${t.message}")
        }
    }


    fun getPagosHoy(onComplete: (Boolean, String, Double?, Boolean) -> Unit)
    {
        var isCorrect = false
        var pagosTotalesHoy = 0.0

        try {

            detailLoanProvider.getDetailLoanByDate(getFechaActualNormalCalendar()).addOnSuccessListener {

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

            detailLoanProvider.getDetailLoanByDate(getYesterdayFechaNormal()).addOnSuccessListener {

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
}