package com.paparazziapps.pretamistapp.modulos.registro.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.QuerySnapshot
import com.paparazziapps.pretamistapp.modulos.tesoreria.pojo.DetallePrestamoSender
import com.paparazziteam.yakulap.helper.applicacion.MyPreferences

class DetallePrestamoProvider {

    var preferences = MyPreferences()

    companion object {
        private lateinit var mCollectionDetallePrestamo:CollectionReference
    }

    //Constructor
    init {
        mCollectionDetallePrestamo = FirebaseFirestore.getInstance().collection("DetallePrestamo")
    }

    // No need to implemented when is super admin
    fun createDetalle(detallePrestamo: DetallePrestamoSender): Task<Void>
    {
        detallePrestamo.id = mCollectionDetallePrestamo.document().id
        detallePrestamo.sucursalId = preferences.sucursalId
        return mCollectionDetallePrestamo.document(detallePrestamo.id!!).set(detallePrestamo)
    }

    // No need to implemented when is super admin
    fun getDetallePrestamosByFecha(fecha:String):Task<QuerySnapshot>
    {
        return mCollectionDetallePrestamo
            .whereEqualTo("fechaPago",fecha)
            .whereEqualTo("sucursalId",preferences.sucursalId)
            .get()
    }

    // Process
    fun getPrestamosByDate(timeStart:Long, timeEnd:Long, idSucursal:Int): Task<QuerySnapshot>
    {
        return  mCollectionDetallePrestamo
            .whereGreaterThanOrEqualTo("unixtime", timeStart)
            .whereLessThanOrEqualTo("unixtime",timeEnd)
            .whereEqualTo("sucursalId", if(preferences.isSuperAdmin) idSucursal else  preferences.sucursalId)
            .get()
    }
}