package com.paparazziapps.pretamistapp.presentation.login.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.paparazziapps.pretamistapp.application.MyPreferences
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.repository.PARepository
import com.paparazziapps.pretamistapp.domain.Sucursales
import com.paparazziapps.pretamistapp.domain.UserForm
import com.paparazziapps.pretamistapp.helper.INT_DEFAULT
import com.paparazziapps.pretamistapp.helper.toJson
import com.paparazziapps.pretamistapp.presentation.principal.viewmodels.ViewModelPrincipal.UIStatePrincipal
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
        val result = repository.loginWithEmailV2(email, pass)

        when(result) {
            is PAResult.Error -> {
                _message.value = "Usuario y/o contraseña incorrectos"
                _isLoginEmail.value = false
                _isLoading.value = false
                return@launch
            }
            is PAResult.Success -> {

                _isLoginEmail.value = true
                _isLoading.value = false
            }
        }
    }

}
