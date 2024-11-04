package com.paparazziapps.pretamistapp.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.database.DataSnapshot
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.providers.BranchesProvider
import com.paparazziapps.pretamistapp.data.remote.RemoteBranchDataSource
import com.paparazziapps.pretamistapp.data.remote.RemoteDetailLoanDataSource
import com.paparazziapps.pretamistapp.data.remote.RemoteLoanDataSource
import com.paparazziapps.pretamistapp.data.remote.RemoteLoginDataSource
import com.paparazziapps.pretamistapp.data.remote.RemoteUserDataSource
import com.paparazziapps.pretamistapp.domain.DetallePrestamoSender
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.domain.User

class PARepositoryImpl(
    private val remoteBranchDataSource: RemoteBranchDataSource,
    private val remoteDetailLoanDataSource: RemoteDetailLoanDataSource,
    private val remoteLoanDataSource: RemoteLoanDataSource,
    private val remoteLoginDataSource: RemoteLoginDataSource,
    private val remoteUserDataSource: RemoteUserDataSource
) : PARepository {
    override suspend fun geBranchesRepo(): PAResult<DataSnapshot> {
        return remoteBranchDataSource.geBranchesRepo()
    }

    override suspend fun createDetail(detailLoan: DetallePrestamoSender): PAResult<Void> {
        return remoteDetailLoanDataSource.createDetail(detailLoan)
    }

    override suspend fun getDetailLoanByDate(date: String): PAResult<QuerySnapshot> {
        return remoteDetailLoanDataSource.getDetailLoanByDate(date)
    }

    override suspend fun getLoanByDate(
        timeStart: Long,
        timeEnd: Long,
        idBranch: Int
    ): PAResult<QuerySnapshot> {
        return remoteDetailLoanDataSource.getLoanByDate(timeStart, timeEnd, idBranch)
    }

    override suspend fun createLoan(loanDomain: LoanDomain, idBranch: Int): PAResult<Void> {
        return remoteLoanDataSource.createLoan(loanDomain, idBranch)
    }

    override suspend fun getLoans(): PAResult<QuerySnapshot> {
        return remoteLoanDataSource.getLoans()
    }

    override suspend fun setLastPayment(
        idLoan:String,
        dateLastPaymentNew:String,
        quotesPendingNew:Int,
        quotesPaidNew:Int
    ): PAResult<Void> {
        return remoteLoanDataSource.setLastPayment(idLoan, dateLastPaymentNew, quotesPendingNew, quotesPaidNew)
    }

    override suspend fun setLastPaymentForQuota(
        idLoan:String,
        dateLastPaymentNew:String,
        quotesPendingNew:Int,
        quotesPaidNew:Int
    ): PAResult<Void> {
        return remoteLoanDataSource.setLastPaymentForQuota(idLoan, dateLastPaymentNew, quotesPendingNew, quotesPaidNew)
    }

    override suspend fun closeLoan(id: String): PAResult<Void> {
        return remoteLoanDataSource.closeLoan(id)
    }

    override suspend fun loginEmail(email: String, pass: String): PAResult<AuthResult> {
        return remoteLoginDataSource.loginEmail(email, pass)
    }

    override suspend fun loginAnonymously(): PAResult<AuthResult> {
        return remoteLoginDataSource.loginAnonymously()
    }

    override suspend fun createUser(email: String, pass: String): PAResult<AuthResult> {
        return remoteLoginDataSource.createUser(email, pass)
    }

    override suspend fun createUser(user: User): PAResult<Void> {
        return remoteUserDataSource.createUser(user)
    }

    override fun signOut() {
        remoteLoginDataSource.signOut()
    }

    override suspend fun searchUserByEmail(email: String): PAResult<DocumentSnapshot> {
        return remoteUserDataSource.searchUserByEmail(email)
    }

}