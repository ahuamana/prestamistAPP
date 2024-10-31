package com.paparazziapps.pretamistapp.modulos.principal.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paparazziapps.pretamistapp.application.MyPreferences
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.User
import com.paparazziapps.pretamistapp.data.providers.LoginProvider
import com.paparazziapps.pretamistapp.data.providers.UserProvider
import kotlinx.coroutines.launch

class ViewModelPrincipal(
    private val preferences: MyPreferences,
    private val userProvider: UserProvider,
) : ViewModel(){

    private val _user = MutableLiveData<User>()

    private val _message = MutableLiveData<String>()

    fun showMessage(): LiveData<String> {
        return _message
    }

    fun getUser(): LiveData<User> {
        return _user
    }

    fun searchUserByEmail() = viewModelScope.launch {
        val email = preferences.getEmail()

        if(email.isEmpty()) {
            _message.setValue("El email del usuario se encuentra vacio")
            return@launch
        }

        when(val result = userProvider.searchUserByEmail(email)){
            is PAResult.Error -> {
                _message.setValue("Ah ocurrido un error al traer los datos del usurio")
            }
            is PAResult.Success -> {
                val user = result.data.toObject(User::class.java)
                if(user == null){
                    _message.setValue("No se encontro el usuario")
                    return@launch
                }
                user.let {
                    _user.value = it
                }
            }
        }
    }
}