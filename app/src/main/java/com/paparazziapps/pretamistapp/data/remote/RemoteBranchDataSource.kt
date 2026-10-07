package com.paparazziapps.pretamistapp.data.remote

import com.paparazziapps.pretamistapp.data.network.GlobalResponse
import com.paparazziapps.pretamistapp.data.network.PAResult

interface RemoteBranchDataSource {
    suspend fun geBranchesRepo(): PAResult<GlobalResponse>
}