package com.paparazziapps.pretamistapp.data.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.paparazziapps.pretamistapp.data.PADataConstants

class BranchesProvider {

    companion object{
        private lateinit var database: DatabaseReference
    }

    init {
        database = Firebase.database.reference.child(PADataConstants.BRANCHES)
    }

    fun getSucursalesRepo(): Task<DataSnapshot> {
        return database.get()
    }
}