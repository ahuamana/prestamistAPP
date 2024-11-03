package com.paparazziapps.pretamistapp.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.database.DataSnapshot
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.DetallePrestamoSender
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.domain.User

interface PARepository {
    //Branches
    suspend fun geBranchesRepo(): PAResult<DataSnapshot>

    //DetailLoan
    suspend fun createDetail(detailLoan: DetallePrestamoSender): PAResult<Void>
    suspend fun getDetailLoanByDate(date:String):PAResult<QuerySnapshot>
    suspend fun getLoanByDate(timeStart:Long, timeEnd:Long, idBranch:Int): PAResult<QuerySnapshot>

    //Loan
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

    //Login
    suspend fun loginEmail(email: String, pass: String): PAResult<AuthResult>
    suspend fun loginAnonymously(): PAResult<AuthResult>
    suspend fun createUser(email: String, pass: String): PAResult<AuthResult>
    fun signOut()

    //User
    suspend fun createUser(user: User): PAResult<Void>
    suspend fun searchUserByEmail(email: String): PAResult<DocumentSnapshot>
}