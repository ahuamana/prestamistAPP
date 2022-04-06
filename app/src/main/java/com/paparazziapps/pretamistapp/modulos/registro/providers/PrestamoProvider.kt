package com.paparazziapps.pretamistapp.modulos.registro.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.QuerySnapshot
import com.paparazziapps.pretamistapp.modulos.registro.pojo.Prestamo

class PrestamoProvider {

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


    fun create(prestamo: Prestamo): Task<Void>
    {
        prestamo.id = mCollectionPrestamo.document().id
        return mCollectionPrestamo.document(prestamo.id!!).set(prestamo)
    }

    fun getPrestamos(): Task<QuerySnapshot>
    {
      return  mCollectionPrestamo.whereEqualTo("state", "ABIERTO").get()
    }

    fun setLastPayment(id:String, fecha:String,diasRestantesPorPagar:Int, diasPagados:Int): Task<Void>
    {
        val map = mutableMapOf<String,Any?>()
        map.put("fechaUltimoPago",fecha)
        map.put("dias_restantes_por_pagar",diasRestantesPorPagar)
        map.put("diasPagados",diasPagados)

        return mCollectionPrestamo.document(id).update(map)
    }

    fun cerrarPrestamo(id: String):Task<Void>
    {
        val map = mutableMapOf<String,Any?>()
        map.put("state","CERRADO")

        return mCollectionPrestamo.document(id).update(map)
    }


}