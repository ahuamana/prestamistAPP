package com.paparazziapps.pretamistapp.data.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.UserForm

interface RemoteUserDataSource {
    suspend fun createUser(userForm: UserForm): PAResult<Void>
    suspend fun searchUserByEmail(email: String): PAResult<DocumentSnapshot>
}