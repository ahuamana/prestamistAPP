package com.paparazziapps.pretamistapp.data.remote

import com.google.firebase.firestore.QuerySnapshot
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.providers.LoanProvider
import com.paparazziapps.pretamistapp.domain.LoanDomain

class RemoteLoanDataSourceImpl(
    private val loanProvider: LoanProvider,
) : RemoteLoanDataSource {
    override suspend fun createLoan(loanDomain: LoanDomain, idBranch: Int): PAResult<Void> {
        return loanProvider.createLoan(loanDomain, idBranch)
    }

    override suspend fun getLoans(): PAResult<QuerySnapshot> {
        return loanProvider.getLoans()
    }

    override suspend fun setLastPayment(
        id: String,
        date: String,
        daysMissingToPay: Int,
        paidDays: Int
    ): PAResult<Void> {
        return loanProvider.setLastPayment(id, date, daysMissingToPay, paidDays)
    }

    override suspend fun setLastPaymentForQuota(
        id: String,
        date: String,
        daysMissingToPay: Int,
        paidDays: Int,
        quotesPaid: Int
    ): PAResult<Void> {
        return loanProvider.setLastPaymentForQuota(id, date, daysMissingToPay, paidDays, quotesPaid)
    }

    override suspend fun closeLoan(id: String): PAResult<Void> {
        return loanProvider.closeLoan(id)
    }


}