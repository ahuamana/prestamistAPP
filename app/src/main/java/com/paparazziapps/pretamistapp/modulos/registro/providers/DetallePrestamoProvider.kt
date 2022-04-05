package com.paparazziapps.pretamistapp.modulos.registro.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.QuerySnapshot
import com.paparazziapps.pretamistapp.modulos.tesoreria.pojo.DetallePrestamoSender

class DetallePrestamoProvider {

    companion object {
        private lateinit var mCollectionDetallePrestamo:CollectionReference
    }

    //Constructor
    init {
        mCollectionDetallePrestamo = FirebaseFirestore.getInstance().collection("DetallePrestamo")
    }

    fun createDetalle(detallePrestamo: DetallePrestamoSender): Task<Void>
    {
        detallePrestamo.id = mCollectionDetallePrestamo.document().id
        return mCollectionDetallePrestamo.document(detallePrestamo.id!!).set(detallePrestamo)
    }

    fun getDetallePrestamos():Task<QuerySnapshot>
    {
        return mCollectionDetallePrestamo.get()
    }
}