package com.paparazziapps.pretamistapp.data.providers

import android.util.Log
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.app.MyPreferences
import com.paparazziapps.pretamistapp.data.network.GlobalResponse
import com.paparazziapps.pretamistapp.libraries.database.firebase.FDatabaseImpl
import com.paparazziapps.pretamistapp.data.network.NetworkOperation
import com.paparazziapps.pretamistapp.data.network.PAResult

class LoanProvider private constructor(
    private val preferences: MyPreferences,
    private val fDatabaseImpl: FDatabaseImpl
) {

    private val tag = LoginProvider::class.java.simpleName

    //Super admin -- implemented
    suspend fun createLoan(loanDomain: LoanDomain, idBranch:Int): PAResult<GlobalResponse> {
        return NetworkOperation.safeApiCall {
            test
        }
    }

    //Super admin -- implemented
    suspend fun getLoans(): PAResult<GlobalResponse> {
        if(preferences.isSuperAdmin){
           return NetworkOperation.safeApiCall {
                  test
            }
        }else{
            Log.d(tag,"Sucursal -- getPrestamos")
            return  NetworkOperation.safeApiCall {
                test
            }
        }
    }

    //No need superAdmin - or Adming to update
    suspend fun setLastPayment(
        idLoan:String,
        dateLastPaymentNew:String,
        quotesPendingNew:Int,
        quotesPaidNew:Int): PAResult<GlobalResponse> {
        val map = mutableMapOf<String,Any?>()
        map.put("lastPaymentDate",dateLastPaymentNew)
        map.put("quotasPending",quotesPendingNew)
        map.put("quotasPaid",quotesPaidNew)

        return NetworkOperation.safeApiCall {
            test
        }
    }

    suspend fun setLastPaymentForQuota(
        idLoan:String,
        dateLastPaymentNew:String,
        quotesPendingNew:Int,
        quotesPaidNew:Int
        ): PAResult<GlobalResponse> {

        val map = mutableMapOf<String,Any?>()
        map.put("lastPaymentDate",dateLastPaymentNew)
        map.put("quotasPending",quotesPendingNew)
        map.put("quotasPaid",quotesPaidNew)

        return NetworkOperation.safeApiCall {
            test
        }
    }



    //No need superAdmin - or Adming to update
    suspend fun closeLoan(id: String): PAResult<GlobalResponse> {
        return NetworkOperation.safeApiCall {
            val map = mutableMapOf<String,Any?>()
            map.put("state","CERRADO")
            test
        }
    }

    companion object {
        val test = GlobalResponse(true)
        fun createInstance(preferences: MyPreferences): LoanProvider{
            return LoanProvider(preferences = preferences, fDatabaseImpl = FDatabaseImpl.Companion.instance())
        }
    }
}