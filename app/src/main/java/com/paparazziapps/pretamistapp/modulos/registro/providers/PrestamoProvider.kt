package com.paparazziapps.pretamistapp.modulos.registro.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.QuerySnapshot
import com.paparazziapps.pretamistapp.modulos.registro.pojo.LoanResponse
import com.paparazziapps.pretamistapp.application.MyPreferences

class PrestamoProvider {

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
    fun create(loanResponse: LoanResponse, idSucursal:Int): Task<Void> {
        loanResponse.sucursalId = if(preferences.isSuperAdmin) idSucursal else  preferences.sucursalId
        loanResponse.id = mCollectionPrestamo.document().id
        return mCollectionPrestamo.document(loanResponse.id!!).set(loanResponse)
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

    //No need superAdmin - or Adming to update
    fun cerrarPrestamo(id: String):Task<Void> {
        val map = mutableMapOf<String,Any?>()
        map.put("state","CERRADO")
        return mCollectionPrestamo.document(id).update(map)
    }
}