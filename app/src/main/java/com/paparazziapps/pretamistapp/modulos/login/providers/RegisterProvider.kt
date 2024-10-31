package com.paparazziapps.pretamistapp.modulos.login.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

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