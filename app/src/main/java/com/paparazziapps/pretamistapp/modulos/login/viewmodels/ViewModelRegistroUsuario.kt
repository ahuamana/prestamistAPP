package com.paparazziapps.pretamistapp.modulos.login.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseUser
import com.paparazziapps.pretamistapp.modulos.login.pojo.User
import com.paparazziapps.pretamistapp.modulos.login.providers.RegisterProvider
import com.paparazziapps.pretamistapp.modulos.login.providers.UserProvider

class ViewModelRegistroUsuario private constructor() {

    var user: FirebaseUser? = null
    var mUserProvider = UserProvider()
    var mRegisterProvider = RegisterProvider()

    private val _message = MutableLiveData<String>()

    private val _user = MutableLiveData<FirebaseUser>()

    private val _isLoading = MutableLiveData<Boolean>()

    private val _isSavedFirebase = MutableLiveData<Boolean>()

    fun getIsSavedFirebase(): LiveData<Boolean> {
        return _isSavedFirebase
    }

    fun getIsLoading(): LiveData<Boolean> {
        return _isLoading
    }

    fun showMessage(): LiveData<String> {
        return _message
    }

    fun getUser(): LiveData<FirebaseUser> {
        return _user
    }

    fun createUser(email: String, pass: String) {
        _isLoading.value = true

        try {
            mRegisterProvider.createUser(email,pass).addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    user = task.result.user
                    _user.setValue(user)
                } else {
                    // If sign in fails, display a message to the user.
                    _message.setValue("Ah ocurrido un error al intentar crear un usuario nuevo")
                    _isLoading.setValue(false)
                }

                }.addOnFailureListener { e ->
                _message.setValue(e.message)
                _isLoading.setValue(false)
            }
        } catch (e: Exception) {
            _message.setValue(e.message)
        }
    }

    fun saveFirebaseUser(usernew: User) {
        try {
            mUserProvider.create(usernew).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _isLoading.setValue(false)
                    _isSavedFirebase.setValue(true)
                } else {
                    _isLoading.setValue(false)
                    _isSavedFirebase.setValue(false)
                }
            }.addOnFailureListener{ e ->
                _isLoading.setValue(false)
                _message.setValue(e.message)
                _isSavedFirebase.setValue(false)
            }

        } catch (e: java.lang.Exception) {
            _isLoading.setValue(false)
            _message.setValue(e.message)
            _isSavedFirebase.setValue(false)
        }
    }


    companion object Singleton{
        private var instance: ViewModelRegistroUsuario? = null

        fun getInstance(): ViewModelRegistroUsuario =
            instance ?: ViewModelRegistroUsuario(
                //local y remoto
            ).also {
                instance = it
            }

        fun destroyInstance(){
            instance = null
        }
    }
}