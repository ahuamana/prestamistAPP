package com.paparazziapps.pretamistapp.presentation.registro.views

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.repository.PAClientsRepository
import com.paparazziapps.pretamistapp.domain.clients.ClientDomainSelect
import com.paparazziapps.pretamistapp.domain.clients.toClientDomainSelect
import com.paparazziapps.pretamistapp.helper.base.BaseViewModel

class SelectUserViewModel(
    val stateHandle: SavedStateHandle,
    private val clientsRepository: PAClientsRepository
) : BaseViewModel() {

    //Save info from loan
    private var clientSelected: ClientDomainSelect? = null

    private var listClients: List<ClientDomainSelect>? = null

    fun getClients() = launchDataLoad {
        _uiState.value = UiState.Loading
        val clients = clientsRepository.getClientsOnlyFirst20()
        when (clients) {
            is PAResult.Error -> {
                _uiState.value = UiState.Error(clients.exception)
            }
            is PAResult.Success -> {
                _uiState.value = UiState.Success(clients.data.toClientDomainSelect())
                listClients = clients.data.toClientDomainSelect()
                clientSelected= null
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
                _uiState.value = UiState.Success(clients.data.toClientDomainSelect())
                listClients = clients.data.toClientDomainSelect()
                clientSelected= null
            }
        }
    }

    fun saveClient(client: ClientDomainSelect) {
        clientSelected = client
        updateClientSelected(client)
    }

    private fun updateClientSelected(client: ClientDomainSelect) {
        // Create a new list with updated selection
        listClients = listClients?.map { domain ->
            // Create a new object to ensure immutability
            domain.copy(isSelected = domain.id == client.id)
        }

        // Update UI state

        Log.d("SelectUserViewModel", "Clients: $listClients")
        _uiState.value = UiState.Success(listClients)
    }
}