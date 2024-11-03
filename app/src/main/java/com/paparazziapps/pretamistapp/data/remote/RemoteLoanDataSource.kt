package com.paparazziapps.pretamistapp.data.remote

import com.google.firebase.firestore.QuerySnapshot
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.LoanDomain

interface RemoteLoanDataSource {
    suspend fun createLoan(loanDomain: LoanDomain, idBranch:Int): PAResult<Void>
    suspend fun getLoans(): PAResult<QuerySnapshot>
    suspend fun setLastPayment(idLoan:String,
                               dateLastPaymentNew:String,
                               quotesPendingNew:Int,
                               quotesPaidNew:Int): PAResult<Void>
    suspend fun setLastPaymentForQuota(
        idLoan:String,
        dateLastPaymentNew:String,
        quotesPendingNew:Int,
        quotesPaidNew:Int): PAResult<Void>
    suspend fun closeLoan(id: String): PAResult<Void>
}