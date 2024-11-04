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
        idLoan:String,
        dateLastPaymentNew:String,
        quotesPendingNew:Int,
        quotesPaidNew:Int
    ): PAResult<Void> {
        return loanProvider.setLastPayment(idLoan, dateLastPaymentNew, quotesPendingNew, quotesPaidNew)
    }

    override suspend fun setLastPaymentForQuota(
        idLoan:String,
        dateLastPaymentNew:String,
        quotesPendingNew:Int,
        quotesPaidNew:Int
    ): PAResult<Void> {
        return loanProvider.setLastPaymentForQuota(idLoan, dateLastPaymentNew, quotesPendingNew, quotesPaidNew)
    }

    override suspend fun closeLoan(id: String): PAResult<Void> {
        return loanProvider.closeLoan(id)
    }


}