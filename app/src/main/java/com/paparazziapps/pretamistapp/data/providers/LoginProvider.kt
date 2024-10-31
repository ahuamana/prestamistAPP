package com.paparazziapps.pretamistapp.data.providers

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class LoginProvider {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    fun getIsLogin(): Boolean {
        return mAuth.currentUser != null
    }

    fun getEmail(): String? {
        return if(mAuth.currentUser != null) { mAuth.currentUser!!.email } else ""
    }

    suspend fun loginEmail(email:String, pass:String): AuthResult? {
       return mAuth.signInWithEmailAndPassword(email, pass).await()
    }

    suspend fun loginAnonymously(): AuthResult? {
        return  mAuth.signInAnonymously().await()
    }

    fun signOut() {
        return mAuth.signOut()
    }

}