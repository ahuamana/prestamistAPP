package com.paparazziapps.pretamistapp.modulos.login.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.paparazziapps.pretamistapp.helper.fromJson
import com.paparazziapps.pretamistapp.helper.toJson
import com.paparazziapps.pretamistapp.modulos.login.pojo.Sucursales
import com.paparazziapps.pretamistapp.modulos.login.pojo.SucusarBody
import com.paparazziapps.pretamistapp.modulos.login.providers.SucursalesProvider
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

class ViewModelRegistro private constructor(){


    private var mProviderSucursal = SucursalesProvider()

    var _sucursales = MutableLiveData<List<String>>()

    fun showSucursales() : LiveData<List<String>> {
        return  _sucursales
    }

    fun getSucursales(){

        try {

            mProviderSucursal.getSucursalesRepo().addOnCompleteListener {

                var json = it.result.value.toString().replace("{","").replace("}","").replace("name=","")
                var sinbrackets =json.substring(1,json.lastIndex)
                var array = sinbrackets.split(", ")

                array.forEach {
                    println("Sucursal: $it")
                }
                _sucursales.value = array

            }
        }catch (t:Throwable) {
            println("Error: ${t.message}")
        }

    }


    companion object Singleton{
        private var instance: ViewModelRegistro? = null
        private lateinit var database: DatabaseReference

        fun getInstance(): ViewModelRegistro =
            instance ?: ViewModelRegistro(
                //local y remoto
            ).also {
                instance = it
                database = Firebase.database.reference
            }

        fun destroyInstance(){
            instance = null
        }
    }
}