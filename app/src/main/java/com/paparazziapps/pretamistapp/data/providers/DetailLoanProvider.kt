package com.paparazziapps.pretamistapp.data.providers

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot
import com.paparazziapps.pretamistapp.domain.DetallePrestamoSender
import com.paparazziapps.pretamistapp.application.MyPreferences
import com.paparazziapps.pretamistapp.data.PADataConstants
import com.paparazziapps.pretamistapp.data.di.FirebaseService
import com.paparazziapps.pretamistapp.data.network.NetworkOperation
import com.paparazziapps.pretamistapp.data.network.PAResult
import kotlinx.coroutines.tasks.await

class DetailLoanProvider(
    private val preferences: MyPreferences,
    private val firebaseService: FirebaseService
) {

    private val mCollectionDetallePrestamo: CollectionReference by lazy { firebaseService.firestore.collection(PADataConstants.DETAIL_LOAN_COLLECTION) }

    // No need to implemented when is super admin
    suspend fun createDetail(detailLoan: DetallePrestamoSender): PAResult<Void> {
        detailLoan.id = mCollectionDetallePrestamo.document().id
        detailLoan.sucursalId = preferences.branchId


        return NetworkOperation.safeApiCall {
            mCollectionDetallePrestamo.document(detailLoan.id!!).set(detailLoan).await()
        }
    }

    // No need to implemented when is super admin
    suspend fun getDetailLoanByDate(date:String):PAResult<QuerySnapshot> {
        return NetworkOperation.safeApiCall {
            mCollectionDetallePrestamo
                .whereEqualTo("fechaPago",date)
                .whereEqualTo("sucursalId",preferences.branchId)
                .get().await()
        }
    }

    // Completed - Super Admin Implemented
    suspend fun getLoanByDate(timeStart:Long, timeEnd:Long, idBranch:Int): PAResult<QuerySnapshot> {
        return  NetworkOperation.safeApiCall {
            mCollectionDetallePrestamo
                .whereGreaterThanOrEqualTo("unixtime", timeStart)
                .whereLessThanOrEqualTo("unixtime",timeEnd)
                .whereEqualTo("sucursalId", if(preferences.isSuperAdmin) idBranch else  preferences.branchId)
                .get().await()
        }
    }
}