package com.paparazziapps.pretamistapp.modulos.login.viewmodels


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paparazziapps.pretamistapp.modulos.login.pojo.Sucursal
import com.paparazziapps.pretamistapp.modulos.login.pojo.Sucursales
import com.paparazziapps.pretamistapp.modulos.login.providers.SucursalesProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class ViewModelSucursales private constructor() : ViewModel(){

    private var mProviderSucursal = SucursalesProvider()

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

    fun getSucursales() = viewModelScope.launch(Dispatchers.IO + exceptionHandler){
        val sucursales = ArrayList<Sucursales>()
        val result = mProviderSucursal.getSucursalesRepo().await()

        for (snapshot in result.children) {
            val sucursal = snapshot.getValue(Sucursales::class.java)
            sucursal?.let {
                sucursales.add(it)
            } ?: continue
        }
        _sucursales.postValue(sucursales)
        _sucursalesFinanzas.postValue(sucursales)
    }

    companion object Singleton{
        private var instance: ViewModelSucursales? = null

        fun getInstance(): ViewModelSucursales =
            instance ?: ViewModelSucursales(
                //local y remoto
            ).also {
                instance = it
            }

        fun destroyInstance(){
            instance = null
        }
    }
}

