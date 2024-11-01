package com.paparazziapps.pretamistapp.modulos.login.viewmodels


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.Sucursales
import com.paparazziapps.pretamistapp.data.providers.BranchesProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class ViewModelBranches(
    private val branchesProvider: BranchesProvider
)  : ViewModel(){

    private val tag = ViewModelBranches::class.java.simpleName

    private val _sucursales = MutableLiveData<List<Sucursales>>()
    val sucursales : LiveData<List<Sucursales>> =  _sucursales

    private val _sucursalesFinanzas = MutableLiveData<List<Sucursales>>()
    val sucursalesFinanzas : LiveData<List<Sucursales>> =  _sucursales

   private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e("ViewModelSucursales", "Error: $exception")
        viewModelScope.launch {
            _sucursales.value = emptyList()
            _sucursalesFinanzas.value = emptyList()
        }
    }

    fun getBranches() = viewModelScope.launch(Dispatchers.IO + exceptionHandler){
        val branches = ArrayList<Sucursales>()
        val result = branchesProvider.geBranchesRepo()

        when(result){
            is PAResult.Error -> {
                Log.e(tag, "Error: ${result.exception}")
                _sucursales.postValue(emptyList())
                _sucursalesFinanzas.postValue(emptyList())
            }
            is PAResult.Success -> {
                result.data.children.forEach { snapshot ->
                    val sucursal = snapshot.getValue(Sucursales::class.java)
                    sucursal?.let {
                        branches.add(it)
                    }
                }

                _sucursales.postValue(branches)
                _sucursalesFinanzas.postValue(branches)
            }
        }

    }
}

