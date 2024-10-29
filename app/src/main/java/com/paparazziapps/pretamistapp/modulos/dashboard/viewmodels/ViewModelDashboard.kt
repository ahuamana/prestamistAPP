package com.paparazziapps.pretamistapp.modulos.dashboard.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.toObject
import com.paparazziapps.pretamistapp.helper.getFechaActualNormalInUnixtime
import com.paparazziapps.pretamistapp.modulos.registro.pojo.LoanDomain
import com.paparazziapps.pretamistapp.modulos.registro.providers.DetallePrestamoProvider
import com.paparazziapps.pretamistapp.modulos.registro.providers.PrestamoProvider
import com.paparazziapps.pretamistapp.modulos.tesoreria.pojo.DetallePrestamoSender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ViewModelDashboard private constructor() : ViewModel(){

    private val tag = ViewModelDashboard::class.java.simpleName
    private val loanProvider = PrestamoProvider()
    private var detailLoanProvider = DetallePrestamoProvider()
    private var _loans = MutableStateFlow<MutableList<LoanDomain>>(mutableListOf())
    val loans: StateFlow<MutableList<LoanDomain>> = _loans

    fun getLoans() {
        try {
            val loans = mutableListOf<LoanDomain>()
            loanProvider.getPrestamos().addOnSuccessListener { querySnapshot ->
                if(querySnapshot.isEmpty) {
                    Log.d(tag," lista prestamos esta vacia")
                    return@addOnSuccessListener
                }
                querySnapshot.forEach { document->
                    loans.add(document.toObject())
                    Log.d(tag," lista prestamos ${loans.size}")
                }

                Log.d(tag,"ViewModel --->_Prestamos: ${loans.size}")
                this._loans.value = loans
            }

        }catch (t:Throwable) {
            Log.d(tag,"Error: ${t.message}")
        }

    }

    fun updateUltimoPago(id:String?, fecha:String?, pagoTotal:Double,diasRestantesPorPagar:Int, diasPagadosNuevo:Int,onComplete: (Boolean, String, String?, Boolean) -> Unit) {
        var isCorrect:Boolean

        try {

            loanProvider.setLastPayment(id?:"",fecha?:"",diasRestantesPorPagar,diasPagadosNuevo).addOnCompleteListener {
                if(it.isSuccessful)
                {
                    var detalle = DetallePrestamoSender(
                        idPrestamo = id,
                        fechaPago = fecha,
                        pagoTotal = pagoTotal,
                        unixtime = getFechaActualNormalInUnixtime()
                    )

                    detailLoanProvider.createDetalle(detalle).addOnCompleteListener {
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
                        Log.d(tag,"Error: ${it.message}")
                        isCorrect = false
                        onComplete(isCorrect, "No se pudo crear el ultimo pago, porfavor comuníquese con soporte!", null, false)
                    }

                }else
                {
                    Log.d(tag,"ViewModelRegister --> : Error ${it.exception?.message}")
                    //_message.value = "No se pudo actualizar el pago, intentelo otra vez"
                    isCorrect = false
                    onComplete(isCorrect, "No se pudo actualizar el pago, inténtelo otra vez", null, false)
                }
            }


        }catch (t:Throwable) {
            isCorrect = false
            onComplete(isCorrect, "No se pudo actualizar el pago, porfavor comuníquese con soporte!", null, false)

            Log.d(tag,"Error throable model ----> ${t.message}")
        }
    }

    fun cerrarPrestamo(id:String?, onComplete: (Boolean, String, String?, Boolean) -> Unit) {
        var isCorrect = false

        try {

            loanProvider.cerrarPrestamo(id?:"").addOnCompleteListener {
                if(it.isSuccessful) {
                    isCorrect = true
                    onComplete(isCorrect, "Se cerro el pago", null, false)

                }else {
                    Log.d(tag,"ViewModelRegister --> : Error ${it.exception?.message}")
                    //_message.value = "No se pudo actualizar el pago, intentelo otra vez"
                    isCorrect = false
                    onComplete(isCorrect, "No se pudo cerrar el pago, inténtelo otra vez", null, false)
                }
            }


        }catch (t:Throwable) {
            isCorrect = false
            onComplete(isCorrect, "No se pudo cerrar el pago, porfavor comuníquese con soporte!", null, false)

            Log.d(tag,"Error throable model ----> ${t.message}")
        }
    }
}