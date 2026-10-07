package com.paparazziapps.pretamistapp.data.remote

import com.paparazziapps.pretamistapp.data.network.GlobalResponse
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.providers.DetailLoanProvider
import com.paparazziapps.pretamistapp.domain.DetailLoanDomain

class RemoteDetailLoanDataSourceImpl(
    private val detailLoanProvider: DetailLoanProvider
) : RemoteDetailLoanDataSource {

    override suspend fun createDetail(detailLoanDomain: DetailLoanDomain): PAResult<GlobalResponse> {
        return detailLoanProvider.createDetail(detailLoanDomain)
    }

    override suspend fun getDetailLoanByDate(date: String): PAResult<GlobalResponse> {
        return detailLoanProvider.getDetailLoanByDate(date)
    }

    override suspend fun getLoanByDate(
        timeStart: Long,
        timeEnd: Long,
        idBranch: Int
    ): PAResult<GlobalResponse> {
       return detailLoanProvider.getLoanByDate(timeStart, timeEnd, idBranch)
    }

    override suspend fun createIdDetailLoan(): PAResult<GlobalResponse> {
        return detailLoanProvider.createIdDetailLoan()
    }

}