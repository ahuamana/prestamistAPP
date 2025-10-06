package com.paparazziapps.pretamistapp.presentation.routes

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.repository.PARepository
import com.paparazziapps.pretamistapp.data.sources.route.RouteDataSource
import com.paparazziapps.pretamistapp.data.sources.route.RouteDomainSource
import com.paparazziapps.pretamistapp.helper.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RouteParentViewModel(
    private val routeRepository: PARepository
) : BaseViewModel() {

    private val _routes = MutableStateFlow<List<RouteDomainSource>>(emptyList())
    val routes: StateFlow<List<RouteDomainSource>> get() = _routes.asStateFlow()

    fun getAllRoutes(name: String) = viewModelScope.launch {

        val result = routeRepository.getAllRoutes()
        when(result){
            is PAResult.Error -> {
                _uiState.value = UiState.Error(result.exception)
            }
            is PAResult.Success -> {
                val routes = if(name.isEmpty()){
                    result.data
                } else {
                    result.data.filter { it.name.contains(name, ignoreCase = true) }
                }
                _uiState.value = UiState.Success(routes)
                _routes.value = RouteDataSource.toList(routes)
            }
        }

    }

}