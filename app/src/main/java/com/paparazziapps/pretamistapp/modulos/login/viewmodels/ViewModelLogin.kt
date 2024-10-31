package com.paparazziapps.pretamistapp.modulos.login.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.paparazziapps.pretamistapp.data.providers.LoginProvider

class ViewModelLogin (val handle: SavedStateHandle): ViewModel() {

    private var mLoginProvider = LoginProvider()

    private val _message = MutableLiveData<String>()

    private val _isLoginEmail = MutableLiveData<Boolean>()

    private val _isLoginAnonymous = MutableLiveData<Boolean>()

    private val _isLoading = MutableLiveData<Boolean>()

    fun getIsLoading(): LiveData<Boolean> {
        return _isLoading
    }

    fun showMessage(): LiveData<String> {
        return _message
    }

    fun getIsLoginEmail(): LiveData<Boolean> {
        return _isLoginEmail
    }

    fun getIsLoginAnonymous(): LiveData<Boolean> {
        return _isLoginAnonymous
    }

    fun logout() {
        mLoginProvider.signOut()
    }

    fun isAlreadyLogging(): LiveData<String?> {

        if(mLoginProvider.getIsLogin())
        {
            _message.setValue("Ya tienes un inicio de session")
        }else
        {
            _message.setValue("No haz ingresado")
        }

        return _message

    }

    fun loginWithEmail(email: String?, pass: String?) {
        _isLoading.setValue(true)
        try {
            mLoginProvider.loginEmail(email?:"", pass?:"").addOnCompleteListener {

                if (it.isSuccessful) {
                    _message.value = "Bienvenido"
                    _isLoginEmail.value = true
                    _isLoading.setValue(false)
                } else {
                    _message.value = "Usuario y/o contraseña incorrectos"
                    _isLoginEmail.value = false
                    _isLoading.setValue(false)
                }

            }

        } catch (e: Exception) {
            _message.setValue(e.message)
        }
    }


    fun loginAnonymous() {
        _isLoading.setValue(true)
        try {
            mLoginProvider.loginAnonymously().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _message.setValue("Bienvenido anónimo")
                        try {
                            Thread.sleep(2000)
                        } catch (e: java.lang.Exception) {
                            Log.e("TAG", "Error esperando")
                        }
                        _isLoginAnonymous.setValue(true)
                        _isLoading.setValue(false)
                    } else {
                        _message.setValue("No es posible ingresar. Porfavor contacta con soporte")
                        _isLoginAnonymous.setValue(false)
                        _isLoading.setValue(false)
                    }
                }.addOnFailureListener{
                    e -> _message.setValue("" + e.message)
                }
        } catch (e: java.lang.Exception) {
            Log.e("VM_LOGIN", "Error:" + e.message)
        }
    }

}
