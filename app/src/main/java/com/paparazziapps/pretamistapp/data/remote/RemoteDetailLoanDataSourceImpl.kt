package com.paparazziapps.pretamistapp.data.remote

import com.google.firebase.firestore.QuerySnapshot
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.providers.DetailLoanProvider
import com.paparazziapps.pretamistapp.domain.DetailLoanDomain
import com.paparazziapps.pretamistapp.domain.DetailLoanForm

class RemoteDetailLoanDataSourceImpl(
    private val detailLoanProvider: DetailLoanProvider
) : RemoteDetailLoanDataSource {

    override suspend fun createDetail(detailLoanDomain: DetailLoanDomain): PAResult<DetailLoanForm> {
        return detailLoanProvider.createDetail(detailLoanDomain)
    }

    override suspend fun getDetailLoanByDate(date: String): PAResult<QuerySnapshot> {
        return detailLoanProvider.getDetailLoanByDate(date)
    }

    override suspend fun getLoanByDate(
        timeStart: Long,
        timeEnd: Long,
        idBranch: Int
    ): PAResult<QuerySnapshot> {
       return detailLoanProvider.getLoanByDate(timeStart, timeEnd, idBranch)
    }

    override suspend fun createIdDetailLoan(): PAResult<String> {
        return detailLoanProvider.createIdDetailLoan()
    }

}