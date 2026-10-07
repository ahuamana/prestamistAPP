package com.paparazziapps.pretamistapp.libraries.database.firebase

import com.paparazziapps.pretamistapp.data.network.GlobalResponse

interface FDatabase {
    fun createUserWithEmailAndPassword(username:String, password:String): GlobalResponse

    fun signInWithEmailAndPassword(username:String, password:String): GlobalResponse

    fun signInAnonymously():  GlobalResponse

    fun signOut()
}


internal class FDatabaseImpl : FDatabase {

    override fun createUserWithEmailAndPassword(username: String, password: String): GlobalResponse {
        return test
    }

    override fun signInWithEmailAndPassword(
        username: String,
        password: String
    ): GlobalResponse {
        return test
    }

    override fun signInAnonymously(): GlobalResponse {
        return test
    }

    override fun signOut() {

    }

    companion object {
        val test = GlobalResponse(
            success = true
        )
        fun instance(): FDatabaseImpl{
            return FDatabaseImpl()
        }
    }
}