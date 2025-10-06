package com.paparazziapps.pretamistapp.presentation.routes

import androidx.lifecycle.viewModelScope
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.repository.PARepository
import com.paparazziapps.pretamistapp.data.sources.route.RouteDomainSource
import com.paparazziapps.pretamistapp.helper.base.BaseViewModel
import kotlinx.coroutines.launch

class NewRouteViewModel (private val routeRepository: PARepository
) : BaseViewModel() {


    fun createRoute(name: String) = viewModelScope.launch {
        val domain = RouteDomainSource(
            id = System.currentTimeMillis().toString(),
            name = name
        )

        val result = routeRepository.createRoute(domain)
        when(result){
            is PAResult.Error -> {
                _uiState.value = UiState.Error(result.exception)
                return@launch
            }
            is PAResult.Success<*> -> {
                // Successfully created
                _uiState.value = UiState.Success(true)
            }
        }

    }

}