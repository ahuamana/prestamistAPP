package com.paparazziapps.pretamistapp.presentation.clients.adater

import com.paparazziapps.pretamistapp.domain.clients.ClientDomain

interface SetOnClickListenerClient {
    fun onItemClicked(client: ClientDomain)
}