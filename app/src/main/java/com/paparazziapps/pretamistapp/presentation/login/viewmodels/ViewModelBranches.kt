package com.paparazziapps.pretamistapp.presentation.login.viewmodels


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.Sucursales
import com.paparazziapps.pretamistapp.data.repository.PARepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ViewModelBranches(
    private val repository: PARepository
)  : ViewModel(){

    private val tag = ViewModelBranches::class.java.simpleName

    private val _branches = MutableLiveData<List<Sucursales>>()
    val branches : LiveData<List<Sucursales>> =  _branches

    private val _branchesFinances = MutableLiveData<List<Sucursales>>()
    val branchesFinances : LiveData<List<Sucursales>> =  _branches

   private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e("ViewModelSucursales", "Error: $exception")
        viewModelScope.launch {
            _branches.value = emptyList()
            _branchesFinances.value = emptyList()
        }
    }

    init {
        getBranches()
    }

    fun getBranches() = viewModelScope.launch(Dispatchers.IO + exceptionHandler){
        val branches = ArrayList<Sucursales>()
        val result = repository.geBranchesRepo()

        when(result){
            is PAResult.Error -> {
                Log.e(tag, "Error: ${result.exception}")
                _branches.postValue(emptyList())
                _branchesFinances.postValue(emptyList())
            }
            is PAResult.Success -> {
                result.data.children.forEach { snapshot ->
                    val sucursal = snapshot.getValue(Sucursales::class.java)
                    sucursal?.let {
                        branches.add(it)
                    }
                }

                _branches.postValue(branches)
                _branchesFinances.postValue(branches)
            }
        }

    }
}

