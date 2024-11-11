package com.paparazziapps.pretamistapp.data.remote

import com.google.firebase.firestore.QuerySnapshot
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.DetailLoanDomain
import com.paparazziapps.pretamistapp.domain.DetailLoanForm

interface RemoteDetailLoanDataSource {
    suspend fun createDetail(detailLoanDomain: DetailLoanDomain): PAResult<DetailLoanForm>
    suspend fun getDetailLoanByDate(date:String):PAResult<QuerySnapshot>
    suspend fun getLoanByDate(timeStart:Long, timeEnd:Long, idBranch:Int): PAResult<QuerySnapshot>
    suspend fun createIdDetailLoan(): PAResult<String>
}