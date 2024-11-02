package com.paparazziapps.pretamistapp.data.remote

import com.google.firebase.database.DataSnapshot
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.providers.BranchesProvider

class RemoteBranchDataSourceImpl(
    private val branchesProvider: BranchesProvider
) : RemoteBranchDataSource {
    override suspend fun geBranchesRepo(): PAResult<DataSnapshot> {
        return branchesProvider.geBranchesRepo()
    }
}