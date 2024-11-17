package com.paparazziapps.pretamistapp.data.providers

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot
import com.paparazziapps.pretamistapp.domain.DetailLoanForm
import com.paparazziapps.pretamistapp.application.MyPreferences
import com.paparazziapps.pretamistapp.data.PADataConstants
import com.paparazziapps.pretamistapp.data.di.FirebaseService
import com.paparazziapps.pretamistapp.data.network.NetworkOperation
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.DetailLoanDomain
import com.paparazziapps.pretamistapp.helper.getFechaActualNormalCalendar
import kotlinx.coroutines.tasks.await

class DetailLoanProvider(
    private val preferences: MyPreferences,
    private val firebaseService: FirebaseService
) {

    private val mCollectionDetallePrestamo: CollectionReference by lazy { firebaseService.firestore.collection(PADataConstants.DETAIL_LOAN_COLLECTION) }

    suspend fun createIdDetailLoan(): PAResult<String> {
        return NetworkOperation.safeApiCall {
            mCollectionDetallePrestamo.document().id
        }
    }

    private fun createIdLoan(): String {
        return mCollectionDetallePrestamo.document().id
    }

    suspend fun createDetail(detailLoanDomain: DetailLoanDomain): PAResult<DetailLoanForm> {

        val idDetailLoan = createIdLoan()

        val receipt= DetailLoanForm(
            idDetailLoan = idDetailLoan,
            idLoan = detailLoanDomain.idLoan,
            paymentDate = getFechaActualNormalCalendar(),
            totalAmountToPay = detailLoanDomain.totalAmountToPay,
            branchId = preferences.branchId,
            codeOperation = System.currentTimeMillis()
        )

        return NetworkOperation.safeApiCall {
            mCollectionDetallePrestamo.document(idDetailLoan).set(receipt).await()
            receipt
        }
    }

    // No need to implemented when is super admin
    suspend fun getDetailLoanByDate(date:String):PAResult<QuerySnapshot> {
        return NetworkOperation.safeApiCall {
            mCollectionDetallePrestamo
                .whereEqualTo("paymentDate",date)
                .whereEqualTo("branchId",preferences.branchId)
                .get().await()
        }
    }

    // Completed - Super Admin Implemented
    suspend fun getLoanByDate(timeStart:Long, timeEnd:Long, idBranch:Int): PAResult<QuerySnapshot> {
        return  NetworkOperation.safeApiCall {
            mCollectionDetallePrestamo
                .whereGreaterThanOrEqualTo("codeOperation", timeStart)
                .whereLessThanOrEqualTo("codeOperation",timeEnd)
                .whereEqualTo("branchId", if(preferences.isSuperAdmin) idBranch else  preferences.branchId)
                .get().await()
        }
    }
}