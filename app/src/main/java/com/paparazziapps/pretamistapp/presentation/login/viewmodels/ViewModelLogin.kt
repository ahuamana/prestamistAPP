package com.paparazziapps.pretamistapp.presentation.login.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paparazziapps.pretamistapp.application.MyPreferences
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.repository.PARepository
import kotlinx.coroutines.launch


class ViewModelLogin (
    private val repository: PARepository,
    private val preferences: MyPreferences
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

    fun loginWithEmail(email: String, pass: String) = viewModelScope.launch {
        _isLoading.value = true
        val result = repository.loginEmail(email, pass)

        when(result) {
            is PAResult.Error -> {
                _message.value = "Usuario y/o contraseña incorrectos"
                _isLoginEmail.value = false
                _isLoading.value = false
                return@launch
            }
            is PAResult.Success -> {
                //TODO: Save user data
                preferences.setEmail(email)
                preferences.isLogin = true

                //TODO: Save the rest of the user data on preferences

                _message.value = "Bienvenido"
                _isLoginEmail.value = true
                _isLoading.value = false
            }
        }
    }

}
