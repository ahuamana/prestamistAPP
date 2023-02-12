package com.paparazziapps.pretamistapp.modulos.login.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.paparazziapps.pretamistapp.modulos.login.pojo.Sucursal
import com.paparazziapps.pretamistapp.modulos.login.pojo.Sucursales
import com.paparazziapps.pretamistapp.modulos.login.providers.SucursalesProvider


class ViewModelSucursales private constructor(){

    private var mProviderSucursal = SucursalesProvider()

    private val _sucursales = MutableLiveData<List<Sucursales>>()
    val sucursales : LiveData<List<Sucursales>> =  _sucursales

    fun getSucursales(){
        mProviderSucursal.getSucursalesRepo().addOnCompleteListener { result ->
            val sucursales = ArrayList<Sucursales>()
            for (snapshot in result.result.children) {
                val sucursal = snapshot.getValue(Sucursales::class.java)
                sucursal?.let {
                    sucursales.add(it)
                }
            }
            _sucursales.value = sucursales
        }.addOnFailureListener { exception ->
            println("Error: ${exception.message}")
        }
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

