package com.paparazziapps.pretamistapp.presentation.principal.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.paparazziapps.pretamistapp.application.MyPreferences
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.repository.PAAnalyticsRepository
import com.paparazziapps.pretamistapp.domain.UserForm
import com.paparazziapps.pretamistapp.data.repository.PARepository
import com.paparazziapps.pretamistapp.domain.Sucursales
import com.paparazziapps.pretamistapp.helper.INT_DEFAULT
import com.paparazziapps.pretamistapp.helper.toJson
import kotlinx.coroutines.launch

class ViewModelPrincipal(
    private val preferences: MyPreferences,
    private val repository: PARepository,
    private val analyticsRepository: PAAnalyticsRepository
) : ViewModel(){

    private val tag = ViewModelPrincipal::class.java.simpleName
    private val _uiState = MutableLiveData<UIStatePrincipal>()
    val uiState: LiveData<UIStatePrincipal> = _uiState

    init {
        getBranches()
    }

    private fun searchUserByEmail(branchesServer: List<Sucursales>) = viewModelScope.launch {
        val email = preferences.getEmail()

        if(email.isEmpty()) {
            val message = "El email del usuario se encuentra vacio"
            Log.d(tag, message)
            _uiState.postValue(UIStatePrincipal.Error(message))
            return@launch
        }

        when(val result = repository.searchUserByEmail(email)){
            is PAResult.Error -> {
                Log.d(tag, "Ah ocurrido un error al traer los datos del usurio")
                _uiState.postValue(UIStatePrincipal.Error("Ah ocurrido un error al traer los datos del usurio"))
            }
            is PAResult.Success -> {
                val userForm = result.data.toObject(UserForm::class.java)
                if(userForm == null){
                    Log.d(tag, "No se encontro el usuario")
                    _uiState.postValue(UIStatePrincipal.Error("No se encontro el usuario"))
                    return@launch
                }
                preferences.isAdmin = userForm.admin
                preferences.isSuperAdmin = userForm.superAdmin
                preferences.branchId = userForm.branchId?: INT_DEFAULT
                preferences.branchName = userForm.branch?:""
                preferences.isActiveUser = userForm.activeUser

                if(userForm.activeUser){
                    _uiState.postValue(UIStatePrincipal.SuccessActiveUser(branchesServer))
                }else {
                    _uiState.postValue(UIStatePrincipal.SuccessInactiveUser(branchesServer))
                }

            }
        }
    }

    private fun getBranches() = viewModelScope.launch{

        //Loading state
        _uiState.postValue(UIStatePrincipal.Loading)

        val result: PAResult<DataSnapshot> = repository.geBranchesRepo()
        when(result){
            is PAResult.Error -> {
                _uiState.postValue(UIStatePrincipal.Error("Error: ${result.exception}"))
            }
            is PAResult.Success -> {
                val branchesServer = result.data.children.mapNotNull { snapshot ->
                    snapshot.getValue(Sucursales::class.java)
                }

                //Save the branches on the preferences
                if(branchesServer.isNotEmpty()){
                    preferences.branches = branchesServer.toJson()
                }

                //Get Info from the user
                searchUserByEmail(branchesServer)
            }
        }

    }

    fun logEvent(event: String) {
        analyticsRepository.logEvent(event)
    }

    sealed class UIStatePrincipal {
        data object Loading : UIStatePrincipal()
        data class SuccessInactiveUser(val branches: List<Sucursales>) : UIStatePrincipal()
        data class SuccessActiveUser(val branches: List<Sucursales>) : UIStatePrincipal()
        data class Error(val message: String) : UIStatePrincipal()
    }
}