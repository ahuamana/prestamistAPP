package com.paparazziapps.pretamistapp.data.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class RegisterProvider {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    fun createUser(email:String,pass:String): Task<AuthResult> {
        return mAuth.createUserWithEmailAndPassword(email, pass)
    }

}