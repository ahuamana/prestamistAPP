package com.paparazziapps.pretamistapp.data.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paparazziapps.pretamistapp.domain.DetallePrestamoSender
import com.paparazziapps.pretamistapp.application.MyPreferences
import com.paparazziapps.pretamistapp.data.PADataConstants
import com.paparazziapps.pretamistapp.data.network.NetworkOperation
import com.paparazziapps.pretamistapp.data.network.PAResult
import kotlinx.coroutines.tasks.await

class DetailLoanProvider(private val preferences: MyPreferences) {

    private val mCollectionDetallePrestamo: CollectionReference by lazy { FirebaseFirestore.getInstance().collection(PADataConstants.DETAIL_LOAN_COLLECTION) }

    // No need to implemented when is super admin
    suspend fun createDetail(detallePrestamo: DetallePrestamoSender): PAResult<Void> {
        detallePrestamo.id = mCollectionDetallePrestamo.document().id
        detallePrestamo.sucursalId = preferences.branchId


        return NetworkOperation.safeApiCall {
            mCollectionDetallePrestamo.document(detallePrestamo.id!!).set(detallePrestamo).await()
        }
    }

    // No need to implemented when is super admin
    suspend fun getDetailLoanByDate(fecha:String):PAResult<QuerySnapshot> {
        return NetworkOperation.safeApiCall {
            mCollectionDetallePrestamo
                .whereEqualTo("fechaPago",fecha)
                .whereEqualTo("sucursalId",preferences.branchId)
                .get().await()
        }
    }

    // Completed - Super Admin Implemented
    suspend fun getLoanByDate(timeStart:Long, timeEnd:Long, idSucursal:Int): PAResult<QuerySnapshot> {
        return  NetworkOperation.safeApiCall {
            mCollectionDetallePrestamo
                .whereGreaterThanOrEqualTo("unixtime", timeStart)
                .whereLessThanOrEqualTo("unixtime",timeEnd)
                .whereEqualTo("sucursalId", if(preferences.isSuperAdmin) idSucursal else  preferences.branchId)
                .get().await()
        }
    }
}