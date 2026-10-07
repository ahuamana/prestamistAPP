package com.paparazziapps.pretamistapp.data.repository

import com.paparazziapps.pretamistapp.data.network.GlobalResponse
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.sources.route.RouteDataSource
import com.paparazziapps.pretamistapp.data.sources.route.RouteDomainSource
import com.paparazziapps.pretamistapp.domain.DetailLoanDomain
import com.paparazziapps.pretamistapp.domain.DetailLoanForm
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.domain.UserForm

interface PARepository {
    //Branches
    suspend fun geBranchesRepo(): PAResult<GlobalResponse>

    //DetailLoan
    suspend fun createDetail(detailLoanDomain: DetailLoanDomain): PAResult<GlobalResponse>
    suspend fun getDetailLoanByDate(date:String):PAResult<GlobalResponse>
    suspend fun getLoanByDate(timeStart:Long, timeEnd:Long, idBranch:Int): PAResult<GlobalResponse>
    suspend fun createIdDetailLoan(): PAResult<GlobalResponse>

    //Loan
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

    //Login
    suspend fun loginEmail(email: String, pass: String): PAResult<GlobalResponse>
    suspend fun loginWithEmailV2(email: String, pass: String): PAResult<GlobalResponse>
    suspend fun loginAnonymously(): PAResult<GlobalResponse>
    suspend fun createUser(email: String, pass: String): PAResult<GlobalResponse>
    fun signOut()

    //User
    suspend fun createUser(userForm: UserForm): PAResult<GlobalResponse>
    suspend fun searchUserByEmail(email: String): PAResult<GlobalResponse>

    //Routes
    suspend fun createRoute(routeDataSource: RouteDomainSource): PAResult<GlobalResponse>
    suspend fun getAllRoutes(): PAResult<GlobalResponse>
}