package com.paparazziapps.pretamistapp.data.providers

import com.paparazziapps.pretamistapp.data.network.GlobalResponse
import com.paparazziapps.pretamistapp.data.network.NetworkOperation
import com.paparazziapps.pretamistapp.data.network.PAResult


interface BranchesProvider {
    suspend fun geBranchesRepo(): PAResult<GlobalResponse>
    suspend fun getBranches(): PAResult<GlobalResponse>
}

class BranchesProviderImpl:BranchesProvider {

    override suspend fun geBranchesRepo(): PAResult<GlobalResponse> {
        return NetworkOperation.safeApiCall {
            test
        }
    }

    override suspend fun getBranches(): PAResult<GlobalResponse> {
        return PAResult.Success(test)
    }

    companion object {
        val test = GlobalResponse(
            success = true
        )
    }
}