package com.paparazziapps.pretamistapp.data.remote

import com.google.firebase.firestore.QuerySnapshot
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.LoanDomain

interface RemoteLoanDataSource {
    suspend fun createLoan(loanDomain: LoanDomain, idBranch:Int): PAResult<Void>
    suspend fun getLoans(): PAResult<QuerySnapshot>
    suspend fun setLastPayment(id:String, date:String, daysMissingToPay:Int, paidDays:Int): PAResult<Void>
    suspend fun setLastPaymentForQuota(
        id:String,
        date:String,
        daysMissingToPay:Int,
        paidDays:Int,
        quotesPaid:Int): PAResult<Void>
    suspend fun closeLoan(id: String): PAResult<Void>
}