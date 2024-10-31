package com.paparazziapps.pretamistapp.data.remote

import com.paparazziapps.pretamistapp.data.providers.BranchesProvider
import com.paparazziapps.pretamistapp.data.providers.DetailLoanProvider
import com.paparazziapps.pretamistapp.data.providers.LoanProvider

class RemoteDataSourceImpl (
    branchesProvider: BranchesProvider,
    detailLoanProvider: DetailLoanProvider,
    loanProvider: LoanProvider,
    registerProvider: BranchesProvider,
    userProvider: BranchesProvider
) : RemoteDataSource {

}