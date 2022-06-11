package com.paparazziapps.pretamistapp.modulos.login.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.ktx.Firebase
import com.paparazziapps.pretamistapp.modulos.login.pojo.Sucursales

class SucursalesProvider {

    companion object{
        private lateinit var database: DatabaseReference
    }

    init {
        database = Firebase.database.reference.child("Sucursales")
    }

    fun getSucursalesRepo(): Task<DataSnapshot> {
        return database.get()
    }
}