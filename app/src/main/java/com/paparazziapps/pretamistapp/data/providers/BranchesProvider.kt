package com.paparazziapps.pretamistapp.data.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.paparazziapps.pretamistapp.data.PADataConstants

class BranchesProvider {

    private val database: DatabaseReference by lazy { Firebase.database.reference.child(PADataConstants.BRANCHES) }

    fun geBranchesRepo(): Task<DataSnapshot> {
        return database.get()
    }
}