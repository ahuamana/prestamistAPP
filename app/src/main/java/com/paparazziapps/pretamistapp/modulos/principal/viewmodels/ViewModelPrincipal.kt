package com.paparazziapps.pretamistapp.modulos.principal.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.paparazziapps.pretamistapp.domain.User
import com.paparazziapps.pretamistapp.data.providers.LoginProvider
import com.paparazziapps.pretamistapp.data.providers.UserProvider

class ViewModelPrincipal private constructor(){


    var mUserProvider = UserProvider()
    var user = User()
    var mAuth = LoginProvider()

    private val _user = MutableLiveData<User>()

    private val _message = MutableLiveData<String>()

    fun showMessage(): LiveData<String> {
        return _message
    }

    fun getUser(): LiveData<User> {
        return _user
    }

    fun searchUserByEmail() {
        //_isLoading.value = true
        try {
            mUserProvider.searchUserByEmail(mAuth.getEmail()).addOnSuccessListener { task ->

                if(task.exists())
                {
                    user = task.toObject(User::class.java)!!
                    _user.value = user
                } else {
                    // If sign in fails, display a message to the user.
                    _message.setValue("Ah ocurrido un error al traer los datos del usurio")
                    //_isLoading.setValue(false)
                }

            }.addOnFailureListener { e ->
                _message.setValue(e.message)
               // _isLoading.setValue(false)
            }
        } catch (e: Exception) {
            _message.setValue(e.message)
        }
    }


    companion object Singleton{
        private var instance: ViewModelPrincipal? = null

        fun getInstance(): ViewModelPrincipal =
            instance ?: ViewModelPrincipal(
                //local y remoto
            ).also {
                instance = it
            }

        fun destroyInstance(){
            instance = null
        }
    }
}