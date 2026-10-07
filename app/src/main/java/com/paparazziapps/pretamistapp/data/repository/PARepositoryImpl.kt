package com.paparazziapps.pretamistapp.data.repository

import com.paparazziapps.pretamistapp.data.network.GlobalResponse
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.remote.RemoteBranchDataSource
import com.paparazziapps.pretamistapp.data.remote.RemoteDetailLoanDataSource
import com.paparazziapps.pretamistapp.data.remote.RemoteLoanDataSource
import com.paparazziapps.pretamistapp.data.remote.RemoteLoginDataSource
import com.paparazziapps.pretamistapp.data.remote.RemoteUserDataSource
import com.paparazziapps.pretamistapp.data.remote.route.RemoteRouteDataSource
import com.paparazziapps.pretamistapp.data.sources.route.RouteDataSource
import com.paparazziapps.pretamistapp.data.sources.route.RouteDomainSource
import com.paparazziapps.pretamistapp.domain.DetailLoanDomain
import com.paparazziapps.pretamistapp.domain.DetailLoanForm
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.domain.UserForm

class PARepositoryImpl(
    private val remoteBranchDataSource: RemoteBranchDataSource,
    private val remoteDetailLoanDataSource: RemoteDetailLoanDataSource,
    private val remoteLoanDataSource: RemoteLoanDataSource,
    private val remoteLoginDataSource: RemoteLoginDataSource,
    private val remoteUserDataSource: RemoteUserDataSource,
    private val remoteRouteDataSource: RemoteRouteDataSource
) : PARepository {

    override suspend fun geBranchesRepo(): PAResult<GlobalResponse> {
        return remoteBranchDataSource.geBranchesRepo()
    }

    override suspend fun createDetail(detailLoanDomain: DetailLoanDomain): PAResult<GlobalResponse> {
        return remoteDetailLoanDataSource.createDetail(detailLoanDomain)
    }

    override suspend fun getDetailLoanByDate(date: String): PAResult<GlobalResponse> {
        return remoteDetailLoanDataSource.getDetailLoanByDate(date)
    }

    override suspend fun getLoanByDate(
        timeStart: Long,
        timeEnd: Long,
        idBranch: Int
    ): PAResult<GlobalResponse> {
        return remoteDetailLoanDataSource.getLoanByDate(timeStart, timeEnd, idBranch)
    }

    override suspend fun createIdDetailLoan(): PAResult<GlobalResponse> {
        return remoteDetailLoanDataSource.createIdDetailLoan()
    }

    override suspend fun createLoan(loanDomain: LoanDomain, idBranch: Int): PAResult<GlobalResponse> {
        return remoteLoanDataSource.createLoan(loanDomain, idBranch)
    }

    override suspend fun getLoans(): PAResult<GlobalResponse> {
        return remoteLoanDataSource.getLoans()
    }

    override suspend fun setLastPayment(
        idLoan:String,
        dateLastPaymentNew:String,
        quotesPendingNew:Int,
        quotesPaidNew:Int
    ): PAResult<GlobalResponse> {
        return remoteLoanDataSource.setLastPayment(idLoan, dateLastPaymentNew, quotesPendingNew, quotesPaidNew)
    }

    override suspend fun setLastPaymentForQuota(
        idLoan:String,
        dateLastPaymentNew:String,
        quotesPendingNew:Int,
        quotesPaidNew:Int
    ): PAResult<GlobalResponse> {
        return remoteLoanDataSource.setLastPaymentForQuota(idLoan, dateLastPaymentNew, quotesPendingNew, quotesPaidNew)
    }

    override suspend fun closeLoan(id: String): PAResult<GlobalResponse> {
        return remoteLoanDataSource.closeLoan(id)
    }

    override suspend fun loginEmail(email: String, pass: String): PAResult<GlobalResponse> {
        return remoteLoginDataSource.loginEmail(email, pass)
    }

    override suspend fun loginWithEmailV2(email: String, pass: String): PAResult<GlobalResponse> {
        return remoteLoginDataSource.loginWithEmailV2(email, pass)
    }

    override suspend fun loginAnonymously(): PAResult<GlobalResponse> {
        return remoteLoginDataSource.loginAnonymously()
    }

    override suspend fun createUser(email: String, pass: String): PAResult<GlobalResponse> {
        return remoteLoginDataSource.createUser(email, pass)
    }

    override suspend fun createUser(userForm: UserForm): PAResult<GlobalResponse> {
        return remoteUserDataSource.createUser(userForm)
    }

    override fun signOut() {
        remoteLoginDataSource.signOut()
    }

    override suspend fun searchUserByEmail(email: String): PAResult<GlobalResponse> {
        return remoteUserDataSource.searchUserByEmail(email)
    }

    override suspend fun createRoute(routeDataSource: RouteDomainSource): PAResult<GlobalResponse> {
        return remoteRouteDataSource.createRoute(RouteDomainSource.toDataSource(routeDataSource))
    }

    override suspend fun getAllRoutes(): PAResult<GlobalResponse> {
        return remoteRouteDataSource.getAllRoutes()
    }

}