package com.paparazziapps.pretamistapp.data.providers

import android.util.Log
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.paparazziapps.pretamistapp.application.MyPreferences
import com.paparazziapps.pretamistapp.data.di.FirebaseService
import com.paparazziapps.pretamistapp.data.network.NetworkOperation
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.Sucursales
import com.paparazziapps.pretamistapp.domain.UserForm
import com.paparazziapps.pretamistapp.helper.INT_DEFAULT
import com.paparazziapps.pretamistapp.helper.toJson
import kotlinx.coroutines.tasks.await

class LoginProvider(
    private val firebaseService: FirebaseService,
    private val branchesProvider: BranchesProvider,
    private val userProvider: UserProvider,
    private val preferences: MyPreferences
) {

    suspend fun loginEmail(email: String, pass: String): PAResult<AuthResult> {
        return NetworkOperation.safeApiCall {

            val branches = branchesProvider.getBranches()

            firebaseService.auth.signInWithEmailAndPassword(email, pass).await()
        }
    }

    suspend fun loginWithEmailV2(email: String, pass: String): PAResult<UserForm> {
        return NetworkOperation.safeApiCall {

            //Login with email and password
            firebaseService.auth.signInWithEmailAndPassword(email, pass).await()

            //Get branches
            val branches = branchesProvider.getBranches().children.mapNotNull { snapshot ->
                snapshot.getValue(Sucursales::class.java)
            }

            //Save branches
            if(branches.isNotEmpty()) {
                preferences.branches = toJson(branches)
            }

            //not throwing exception, just returning a success result
            val user = userProvider.searchUserByEmailV2(email).toObject(UserForm::class.java)
            user?.let {
                preferences.isAdmin = user.admin
                preferences.isSuperAdmin = user.superAdmin
                preferences.branchId = user.branchId?: INT_DEFAULT
                preferences.branchName = user.branch?:""
                preferences.creationDate = user.dateCreated?:0

                //if is active user = cashier if isAdmin = administator || if isSuperAdmin = superAdmin
                preferences.role = when {
                    user.superAdmin -> "Super Administrador"
                    user.admin -> "Administrador"
                    user.activeUser -> "Cajero"
                    else -> "Cajero"
                }

                Log.d("LoginProvider", "User: $user")


                //Save the user info
                preferences.isActiveUser = user.activeUser
                preferences.isLogin = true
                preferences.names = user.names?:""
                preferences.lastnames = user.lastnames?:""

                preferences.isLogin
                preferences.setEmail(email)
            }

            user?: throw Exception("Usuario no encontrado")
        }
    }

    suspend fun loginAnonymously(): PAResult<AuthResult> {
        return  NetworkOperation.safeApiCall {
            firebaseService.auth.signInAnonymously().await()
        }
    }

    fun signOut() {
        return firebaseService.auth.signOut()
    }

}