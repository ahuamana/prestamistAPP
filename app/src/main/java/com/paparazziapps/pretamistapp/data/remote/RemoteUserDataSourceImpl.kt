package com.paparazziapps.pretamistapp.data.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.providers.UserProvider
import com.paparazziapps.pretamistapp.domain.User

class RemoteUserDataSourceImpl(
    private val userProvider: UserProvider
) : RemoteUserDataSource {
    override suspend fun createUser(user: User): PAResult<Void> {
        return userProvider.createUser(user)
    }

    override suspend fun searchUserByEmail(email: String): PAResult<DocumentSnapshot> {
        return userProvider.searchUserByEmail(email)
    }
}