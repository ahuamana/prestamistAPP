package com.paparazziapps.pretamistapp.data.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.QuerySnapshot
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.application.MyPreferences

class LoanProvider {

    var preferences = MyPreferences()

    companion object {
        private lateinit var mCollectionPrestamo:CollectionReference
    }

    //Constructor
    init {
        mCollectionPrestamo =FirebaseFirestore.getInstance().collection("Prestamos")

        var settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()

        FirebaseFirestore.getInstance().firestoreSettings = settings
    }

    //Super admin -- implemented
    fun create(loanDomain: LoanDomain, idSucursal:Int): Task<Void> {
        loanDomain.sucursalId = if(preferences.isSuperAdmin) idSucursal else  preferences.sucursalId
        loanDomain.id = mCollectionPrestamo.document().id
        return mCollectionPrestamo.document(loanDomain.id!!).set(loanDomain)
    }

    //Super admin -- implemented
    fun getPrestamos(): Task<QuerySnapshot> {
        if(preferences.isSuperAdmin){
            println("Super admin -- getPrestamos")
            return  mCollectionPrestamo
                .whereEqualTo("state", "ABIERTO")
                .get()
        }else{
            println("Sucursal -- getPrestamos")
            return  mCollectionPrestamo
                .whereEqualTo("state", "ABIERTO")
                .whereEqualTo("sucursalId",preferences.sucursalId)
                .get()
        }

    }

    //No need superAdmin - or Adming to update
    fun setLastPayment(id:String, fecha:String,diasRestantesPorPagar:Int, diasPagados:Int): Task<Void> {
        val map = mutableMapOf<String,Any?>()
        map.put("fechaUltimoPago",fecha)
        map.put("dias_restantes_por_pagar",diasRestantesPorPagar)
        map.put("diasPagados",diasPagados)
        return mCollectionPrestamo.document(id).update(map)
    }

    fun setLastPaymentForQuota(
        id:String,
        fecha:String,
        diasRestantesPorPagar:Int,
        diasPagados:Int,
        quotesPaid:Int): Task<Void> {
        val map = mutableMapOf<String,Any?>()
        map.put("fechaUltimoPago",fecha)
        map.put("quotasPending",diasRestantesPorPagar)
        map.put("quotasPaid",quotesPaid)
        map.put("diasPagados",diasPagados)
        return mCollectionPrestamo.document(id).update(map)
    }

    //No need superAdmin - or Adming to update
    fun cerrarPrestamo(id: String):Task<Void> {
        val map = mutableMapOf<String,Any?>()
        map.put("state","CERRADO")
        return mCollectionPrestamo.document(id).update(map)
    }
}