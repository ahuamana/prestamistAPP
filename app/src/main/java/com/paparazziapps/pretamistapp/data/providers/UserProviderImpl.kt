package com.paparazziapps.pretamistapp.data.providers

import com.paparazziapps.pretamistapp.data.network.GlobalResponse
import com.paparazziapps.pretamistapp.libraries.database.firebase.FDatabaseImpl
import com.paparazziapps.pretamistapp.data.network.NetworkOperation
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.UserForm

interface UserProvider {
    suspend fun createUser(userForm: UserForm): PAResult<GlobalResponse>
    suspend fun searchUserByEmail(email: String): PAResult<GlobalResponse>
    suspend fun searchUserByEmailV2(email: String): PAResult<GlobalResponse>
}

class UserProviderImpl private constructor(
    private val fDatabaseImpl: FDatabaseImpl
): UserProvider {

    override suspend fun createUser(userForm: UserForm): PAResult<GlobalResponse> {
        val form = userForm.copy(
            activeUser = false,
            dateCreated = System.currentTimeMillis(),
        )

        return NetworkOperation.safeApiCall {
           test
        }
    }

    override suspend fun searchUserByEmail(email: String): PAResult<GlobalResponse> {
        return NetworkOperation.safeApiCall {
            test
        }
    }

    override suspend fun searchUserByEmailV2(email: String): PAResult<GlobalResponse> {
       return NetworkOperation.safeApiCall {
           test
       }
    }

    companion object {
        const val USERS_COLLECTION = "Users"

        val test = GlobalResponse(
            success = true
        )

        fun instance(): UserProviderImpl{
            return UserProviderImpl(fDatabaseImpl = FDatabaseImpl())
        }
    }
}