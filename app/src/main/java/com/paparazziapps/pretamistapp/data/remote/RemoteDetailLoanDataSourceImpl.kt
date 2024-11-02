package com.paparazziapps.pretamistapp.data.remote

import com.google.firebase.firestore.QuerySnapshot
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.providers.DetailLoanProvider
import com.paparazziapps.pretamistapp.domain.DetallePrestamoSender

class RemoteDetailLoanDataSourceImpl(
    private val detailLoanProvider: DetailLoanProvider
) : RemoteDetailLoanDataSource {
    override suspend fun createDetail(detailLoan: DetallePrestamoSender): PAResult<Void> {
        return detailLoanProvider.createDetail(detailLoan)
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

}