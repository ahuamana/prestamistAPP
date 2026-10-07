package com.paparazziapps.pretamistapp.data.remote

import com.paparazziapps.pretamistapp.data.network.GlobalResponse
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.UserForm

interface RemoteUserDataSource {
    suspend fun createUser(userForm: UserForm): PAResult<GlobalResponse>
    suspend fun searchUserByEmail(email: String): PAResult<GlobalResponse>
}