package com.paparazziapps.pretamistapp.modulos.registro.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
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
        return mCollectionPrestamo.document().set(prestamo)
    }


}