package com.paparazziapps.pretamistapp.data.providers

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
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
    suspend fun create(loanDomain: LoanDomain, idSucursal:Int): PAResult<Void> {
        loanDomain.sucursalId = if(preferences.isSuperAdmin) idSucursal else  preferences.branchId
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
    suspend fun setLastPayment(id:String, fecha:String,diasRestantesPorPagar:Int, diasPagados:Int): PAResult<Void> {
        val map = mutableMapOf<String,Any?>()
        map.put("fechaUltimoPago",fecha)
        map.put("dias_restantes_por_pagar",diasRestantesPorPagar)
        map.put("diasPagados",diasPagados)

        return NetworkOperation.safeApiCall {
            mCollectionLoan.document(id).update(map).await()
        }
    }

    suspend fun setLastPaymentForQuota(
        id:String,
        fecha:String,
        diasRestantesPorPagar:Int,
        diasPagados:Int,
        quotesPaid:Int): PAResult<Void> {

        val map = mutableMapOf<String,Any?>()
        map.put("fechaUltimoPago",fecha)
        map.put("quotasPending",diasRestantesPorPagar)
        map.put("quotasPaid",quotesPaid)
        map.put("diasPagados",diasPagados)


        return NetworkOperation.safeApiCall {
            mCollectionLoan.document(id).update(map).await()
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