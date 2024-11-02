package com.paparazziapps.pretamistapp.data.remote

import com.google.firebase.database.DataSnapshot
import com.paparazziapps.pretamistapp.data.network.PAResult

interface RemoteBranchDataSource {
    suspend fun geBranchesRepo(): PAResult<DataSnapshot>
}