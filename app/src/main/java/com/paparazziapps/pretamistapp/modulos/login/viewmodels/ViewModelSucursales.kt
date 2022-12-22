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
        try {
            mProviderSucursal.getSucursalesRepo().addOnCompleteListener { dataSnapshot->
                try {
                    var sucursales = ArrayList<Sucursales>()
                    for (snapshot in dataSnapshot.result!!.children) {
                        var sucursal = snapshot.getValue(Sucursales::class.java)
                        if(sucursal != null){
                            sucursales.add(sucursal)
                        }
                    }
                    _sucursales.value = sucursales

                }catch (e:Exception){
                    println("Error e: ${e.message}")
                }
            }

        }catch (t:Throwable) {
            println("Error: ${t.message}")
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

