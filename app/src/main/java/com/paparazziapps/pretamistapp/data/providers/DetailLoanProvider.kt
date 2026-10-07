package com.paparazziapps.pretamistapp.data.providers

import com.paparazziapps.pretamistapp.app.MyPreferences
import com.paparazziapps.pretamistapp.data.PADataConstants
import com.paparazziapps.pretamistapp.data.network.GlobalResponse
import com.paparazziapps.pretamistapp.libraries.database.firebase.FDatabaseImpl
import com.paparazziapps.pretamistapp.data.network.NetworkOperation
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.DetailLoanDomain

class DetailLoanProvider private constructor(
    private val preferences: MyPreferences,
    private val fDatabaseImpl: FDatabaseImpl
) {


    suspend fun createIdDetailLoan(): PAResult<GlobalResponse> {
        return NetworkOperation.safeApiCall {
            test
        }
    }

    private suspend fun createIdLoan(): PAResult<GlobalResponse> {
        return NetworkOperation.safeApiCall {
            test
        }
    }

    suspend fun createDetail(detailLoanDomain: DetailLoanDomain): PAResult<GlobalResponse> {
        return NetworkOperation.safeApiCall {
            test
        }
    }

    // No need to implemented when is super admin
    suspend fun getDetailLoanByDate(date:String):PAResult<GlobalResponse> {
        return NetworkOperation.safeApiCall {
            test
        }
    }

    // Completed - Super Admin Implemented
    suspend fun getLoanByDate(timeStart:Long, timeEnd:Long, idBranch:Int): PAResult<GlobalResponse> {
        return  NetworkOperation.safeApiCall {
            test
        }
    }

    companion object {
        val test = GlobalResponse(true)
        fun instance(preferences: MyPreferences): DetailLoanProvider {
            return DetailLoanProvider(preferences, FDatabaseImpl.Companion.instance())
        }
    }
}