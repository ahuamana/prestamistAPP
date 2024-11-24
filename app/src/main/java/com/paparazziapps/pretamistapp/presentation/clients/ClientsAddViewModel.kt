package com.paparazziapps.pretamistapp.presentation.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paparazziapps.pretamistapp.data.repository.PAClientsRepository
import com.paparazziapps.pretamistapp.domain.clients.ClientDomain
import kotlinx.coroutines.launch
import java.util.UUID

class ClientsAddViewModel(
    private val clientsRepository: PAClientsRepository
):ViewModel() {

    fun saveClient(
        document: String,
        name: String,
        email: String,
        phone: String,
        lastName: String,
    ) = viewModelScope.launch {

        val domain = ClientDomain(
            id = UUID.randomUUID().toString(),
            name = name,
            lastName = lastName,
            email = email,
            phone = phone,
            document = document
        )

        val result = clientsRepository.createClient(domain)
    }

}