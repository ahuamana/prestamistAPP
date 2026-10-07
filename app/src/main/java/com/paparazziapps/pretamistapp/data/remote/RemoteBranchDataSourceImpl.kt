package com.paparazziapps.pretamistapp.data.remote

import com.paparazziapps.pretamistapp.data.network.GlobalResponse
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.providers.BranchesProviderImpl

class RemoteBranchDataSourceImpl(
    private val branchesProviderImpl: BranchesProviderImpl
) : RemoteBranchDataSource {
    override suspend fun geBranchesRepo(): PAResult<GlobalResponse> {
        return branchesProviderImpl.geBranchesRepo()
    }
}