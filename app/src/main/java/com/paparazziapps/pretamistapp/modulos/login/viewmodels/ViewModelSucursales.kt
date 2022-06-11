package com.paparazziapps.pretamistapp.modulos.login.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.paparazziapps.pretamistapp.helper.toJson
import com.paparazziapps.pretamistapp.modulos.login.providers.SucursalesProvider

class ViewModelSucursales private constructor(){


    private var mProviderSucursal = SucursalesProvider()

    var _sucursales = MutableLiveData<List<String>>()

    fun showSucursales() : LiveData<List<String>> {
        return  _sucursales
    }

    fun getSucursales(){

        try {
            mProviderSucursal.getSucursalesRepo().addOnCompleteListener {
                try {
                    var j = toJson(it.result.value.toString())
                    var json = it.result.value.toString().replace("{","").replace("}","").replace("name=","")
                    var sinbrackets =json.substring(1,json.lastIndex)
                    var array = sinbrackets.split(", ")
                    array.forEach {
                        println("Sucursal: $it")
                    }
                    _sucursales.value = array

                }catch (e:Exception){
                    println("Error e: ${it.exception}")
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