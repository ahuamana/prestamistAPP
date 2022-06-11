package com.paparazziapps.pretamistapp.modulos.login.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginProvider {

    companion object{
        private lateinit var mAuth: FirebaseAuth
    }

    init {
        mAuth = FirebaseAuth.getInstance()
    }

    fun getIsLogin(): Boolean
    {
        return mAuth.currentUser != null
    }

    fun getEmail(): String?
    {
        if(mAuth.currentUser != null)
        {
            return mAuth.currentUser!!.email
        }else
        {
            return ""
        }

    }

    fun loginEmail(email:String, pass:String): Task<AuthResult> {
       return mAuth.signInWithEmailAndPassword(email, pass)
    }

    fun loginAnonimously(): Task<AuthResult> {
        return  mAuth.signInAnonymously()
    }

    fun signout() {
        return mAuth.signOut()
    }

}