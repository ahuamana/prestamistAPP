package com.paparazziapps.pretamistapp.data.remote

import com.paparazziapps.pretamistapp.data.network.GlobalResponse
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.LoanDomain

interface RemoteLoanDataSource {
    suspend fun createLoan(loanDomain: LoanDomain, idBranch:Int): PAResult<GlobalResponse>
    suspend fun getLoans(): PAResult<GlobalResponse>
    suspend fun setLastPayment(idLoan:String,
                               dateLastPaymentNew:String,
                               quotesPendingNew:Int,
                               quotesPaidNew:Int): PAResult<GlobalResponse>
    suspend fun setLastPaymentForQuota(
        idLoan:String,
        dateLastPaymentNew:String,
        quotesPendingNew:Int,
        quotesPaidNew:Int): PAResult<GlobalResponse>
    suspend fun closeLoan(id: String): PAResult<GlobalResponse>
}