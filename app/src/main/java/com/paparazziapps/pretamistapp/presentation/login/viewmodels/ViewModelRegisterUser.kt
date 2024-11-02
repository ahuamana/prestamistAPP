package com.paparazziapps.pretamistapp.presentation.login.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.paparazziapps.pretamistapp.application.MyPreferences
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.User
import com.paparazziapps.pretamistapp.data.repository.PARepository
import kotlinx.coroutines.launch

class ViewModelRegisterUser(
    private val preferences: MyPreferences,
    private val repository: PARepository
) : ViewModel() {

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

    fun createUser(email: String, pass: String, userInfo:User) = viewModelScope.launch {
        _isLoading.value = true

        val result = repository.createUser(email,pass)

        when(result) {
            is PAResult.Error -> {
                // If sign in fails, display a message to the user.
                _message.setValue("Ah ocurrido un error al intentar crear un usuario nuevo")
                _isLoading.setValue(false)
            }
            is PAResult.Success -> {
                // Sign in success, update UI with the signed-in user's information
                //get the email and save it on the preferences and then on firebase
                preferences.setEmail(email)
                //TODO: Save the rest of the user data on preferences
                saveFirebaseUser(userInfo)
            }
        }
    }

    fun saveFirebaseUser(userInfo: User) = viewModelScope.launch {

        val result = repository.createUser(userInfo)

        when(result){
            is PAResult.Error -> {
                with(_isLoading) { setValue(false) }
                with(_message) { setValue("Ah ocurrido un error al intentar guardar los datos del usuario") }
                _isSavedFirebase.setValue(false)

            }
            is PAResult.Success -> {
                with(_isLoading) { setValue(false) }
                with(_isSavedFirebase) { setValue(true) }
            }
        }
    }
}