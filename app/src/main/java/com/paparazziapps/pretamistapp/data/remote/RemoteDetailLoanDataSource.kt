package com.paparazziapps.pretamistapp.data.remote

import com.paparazziapps.pretamistapp.data.network.GlobalResponse
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.DetailLoanDomain
import com.paparazziapps.pretamistapp.domain.DetailLoanForm

interface RemoteDetailLoanDataSource {
    suspend fun createDetail(detailLoanDomain: DetailLoanDomain): PAResult<GlobalResponse>
    suspend fun getDetailLoanByDate(date:String):PAResult<GlobalResponse>
    suspend fun getLoanByDate(timeStart:Long, timeEnd:Long, idBranch:Int): PAResult<GlobalResponse>
    suspend fun createIdDetailLoan(): PAResult<GlobalResponse>
}