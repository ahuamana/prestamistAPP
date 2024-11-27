package com.paparazziapps.pretamistapp.presentation.clients

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.repository.PAClientsRepository
import com.paparazziapps.pretamistapp.helper.base.BaseViewModel
import kotlinx.coroutines.launch

class ClientsParentViewModel (
    private val clientsRepository: PAClientsRepository
) : BaseViewModel() {



    fun getClients() = launchDataLoad {
            _uiState.value = UiState.Loading
            val clients = clientsRepository.getClientsOnlyFirst20()
            when (clients) {
                is PAResult.Error -> {
                    _uiState.value = UiState.Error(clients.exception)
                }
                is PAResult.Success -> {
                    _uiState.value = UiState.Success(clients.data)
                }
            }
    }

    fun searchClients(document: String) = launchDataLoad {
        _uiState.value = UiState.Loading
        val clients = clientsRepository.searchByClientName(document)
        when (clients) {
            is PAResult.Error -> {
                _uiState.value = UiState.Error(clients.exception)
            }
            is PAResult.Success -> {
                Log.d("ClientsParentViewModel", "Clients: ${clients.data}")
                _uiState.value = UiState.Success(clients.data)
            }
        }
    }

}