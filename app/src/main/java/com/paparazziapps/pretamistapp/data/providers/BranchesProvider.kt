package com.paparazziapps.pretamistapp.data.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.paparazziapps.pretamistapp.data.PADataConstants
import com.paparazziapps.pretamistapp.data.network.NetworkOperation
import com.paparazziapps.pretamistapp.data.network.PAResult
import kotlinx.coroutines.tasks.await

class BranchesProvider {

    private val database: DatabaseReference by lazy {
        Firebase.database.reference.child(PADataConstants.BRANCHES)
    }

    suspend fun geBranchesRepo(): PAResult<DataSnapshot> {
        return NetworkOperation.safeApiCall {
            database.get().await()
        }
    }

    suspend fun getBranches(): DataSnapshot {
       return database.get().await()
    }
}