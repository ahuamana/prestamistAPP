package com.paparazziapps.pretamistapp.data.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paparazziapps.pretamistapp.domain.DetallePrestamoSender
import com.paparazziapps.pretamistapp.application.MyPreferences
import com.paparazziapps.pretamistapp.data.PADataConstants

class DetailLoanProvider(private val preferences: MyPreferences) {

    private val mCollectionDetallePrestamo: CollectionReference by lazy { FirebaseFirestore.getInstance().collection(PADataConstants.DETAIL_LOAN_COLLECTION) }

    // No need to implemented when is super admin
    fun createDetail(detallePrestamo: DetallePrestamoSender): Task<Void> {
        detallePrestamo.id = mCollectionDetallePrestamo.document().id
        detallePrestamo.sucursalId = preferences.sucursalId
        return mCollectionDetallePrestamo.document(detallePrestamo.id!!).set(detallePrestamo)
    }

    // No need to implemented when is super admin
    fun getDetailLoanByDate(fecha:String):Task<QuerySnapshot> {
        return mCollectionDetallePrestamo
            .whereEqualTo("fechaPago",fecha)
            .whereEqualTo("sucursalId",preferences.sucursalId)
            .get()
    }

    // Completed - Super Admin Implemented
    fun getLoanByDate(timeStart:Long, timeEnd:Long, idSucursal:Int): Task<QuerySnapshot> {
        return  mCollectionDetallePrestamo
            .whereGreaterThanOrEqualTo("unixtime", timeStart)
            .whereLessThanOrEqualTo("unixtime",timeEnd)
            .whereEqualTo("sucursalId", if(preferences.isSuperAdmin) idSucursal else  preferences.sucursalId)
            .get()
    }
}