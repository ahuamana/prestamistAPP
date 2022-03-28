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
      return  mCollectionPrestamo.get()
    }

    fun setLastPayment(id:String, fecha:String): Task<Void>
    {
        val map = mutableMapOf<String,Any?>()
        map.put("fechaUltimoPago",fecha)

        return mCollectionPrestamo.document(id).update(map)
    }

}