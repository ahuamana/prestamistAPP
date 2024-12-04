package com.paparazziapps.pretamistapp.helper.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    //create a variable to store the state of the UI
    protected val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    protected fun launchDataLoad(block: suspend CoroutineScope.() -> Unit) {

        viewModelScope.launch {
            try {
                block()
            } catch (error: Throwable) {
                handleError(error)
            }
        }
    }

    protected open fun handleError(error: Throwable) {
        _uiState.value = UiState.GenericError
    }


    sealed class UiState {
        object Idle : UiState()
        data object Loading : UiState()
        data class Success<T>(val data: T) : UiState()
        data class Error(val exception: Throwable) : UiState()
        data object GenericError : UiState()
    }
}