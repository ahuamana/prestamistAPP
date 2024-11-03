package com.paparazziapps.pretamistapp.data.providers

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.application.MyPreferences
import com.paparazziapps.pretamistapp.data.PADataConstants
import com.paparazziapps.pretamistapp.data.di.FirebaseService
import com.paparazziapps.pretamistapp.data.network.NetworkOperation
import com.paparazziapps.pretamistapp.data.network.PAResult
import kotlinx.coroutines.tasks.await

class LoanProvider(
    private val preferences: MyPreferences,
    private val firebaseService: FirebaseService
) {

    private val mCollectionLoan: CollectionReference by lazy { firebaseService.firestore.collection(PADataConstants.LOAN_COLLECTION) }
    private val tag = LoginProvider::class.java.simpleName

    //Super admin -- implemented
    suspend fun createLoan(loanDomain: LoanDomain, idBranch:Int): PAResult<Void> {
        loanDomain.branchId = if(preferences.isSuperAdmin) idBranch else  preferences.branchId
        loanDomain.id = mCollectionLoan.document().id

        return NetworkOperation.safeApiCall {
            mCollectionLoan.document(loanDomain.id!!).set(loanDomain).await()
        }
    }

    //Super admin -- implemented
    suspend fun getLoans(): PAResult<QuerySnapshot> {
        if(preferences.isSuperAdmin){
           return NetworkOperation.safeApiCall {
                  mCollectionLoan
                    .whereEqualTo("state", "ABIERTO")
                    .get().await()
            }
        }else{
            Log.d(tag,"Sucursal -- getPrestamos")
            return  NetworkOperation.safeApiCall {
                mCollectionLoan
                    .whereEqualTo("state", "ABIERTO")
                    .whereEqualTo("sucursalId",preferences.branchId)
                    .get().await()
            }
        }
    }

    //No need superAdmin - or Adming to update
    suspend fun setLastPayment(
        idLoan:String,
        dateLastPaymentNew:String,
        quotesPendingNew:Int,
        quotesPaidNew:Int): PAResult<Void> {
        val map = mutableMapOf<String,Any?>()
        map.put("fechaUltimoPago",dateLastPaymentNew)
        map.put("quotasPending",quotesPendingNew)
        map.put("quotasPaid",quotesPaidNew)

        return NetworkOperation.safeApiCall {
            mCollectionLoan.document(idLoan).update(map).await()
        }
    }

    suspend fun setLastPaymentForQuota(
        idLoan:String,
        dateLastPaymentNew:String,
        quotesPendingNew:Int,
        quotesPaidNew:Int
        ): PAResult<Void> {

        val map = mutableMapOf<String,Any?>()
        map.put("fechaUltimoPago",dateLastPaymentNew)
        map.put("quotasPending",quotesPendingNew)
        map.put("quotasPaid",quotesPaidNew)

        return NetworkOperation.safeApiCall {
            mCollectionLoan.document(idLoan).update(map).await()
        }
    }



    //No need superAdmin - or Adming to update
    suspend fun closeLoan(id: String): PAResult<Void> {
        return NetworkOperation.safeApiCall {
            val map = mutableMapOf<String,Any?>()
            map.put("state","CERRADO")
            mCollectionLoan.document(id).update(map).await()
        }
    }
}