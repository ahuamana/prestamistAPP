package com.paparazziapps.pretamistapp.modulos.login.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.paparazziapps.pretamistapp.modulos.login.pojo.User

class RegisterProvider {

    companion object{
        private lateinit var mAuth: FirebaseAuth
    }

    init {
        mAuth = FirebaseAuth.getInstance();
    }

    fun createUser(email:String,pass:String): Task<AuthResult> {
        return mAuth.createUserWithEmailAndPassword(email, pass)
    }

}