package com.paparazziapps.pretamistapp.modulos.login.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paparazziapps.pretamistapp.data.providers.LoginProvider
import kotlinx.coroutines.launch


class ViewModelLogin (
    private val loginProvider: LoginProvider
    ): ViewModel() {

    private val tag = ViewModelLogin::class.java.simpleName
    private val _message = MutableLiveData<String>()
    private val _isLoginEmail = MutableLiveData<Boolean>()
    private val _isLoginAnonymous = MutableLiveData<Boolean>()
    private val _isLoading = MutableLiveData<Boolean>()

    val isLoading: LiveData<Boolean> = _isLoading
    val showMessage: LiveData<String> = _message
    val isLoginEmail: LiveData<Boolean> = _isLoginEmail
    val isLoginAnonymous: LiveData<Boolean> = _isLoginAnonymous

    fun loginWithEmail(email: String?, pass: String?) = viewModelScope.launch {
        _isLoading.setValue(true)
        try {
            val task = loginProvider.loginEmail(email?:"", pass?:"")

            if(task == null) {
                _message.value = "Usuario y/o contraseña incorrectos"
                _isLoginEmail.value = false
                _isLoading.setValue(false)
                return@launch
            }

            // Si el usuario se loguea correctamente
            _message.value = "Bienvenido"
            _isLoginEmail.value = true
            _isLoading.setValue(false)


        } catch (e: Exception) {
            Log.d(tag, "Error: " + e.message)
            _message.setValue(e.message)
        }
    }

}
