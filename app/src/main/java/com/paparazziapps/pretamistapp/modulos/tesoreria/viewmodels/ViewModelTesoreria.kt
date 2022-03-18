package com.paparazziapps.pretamistapp.modulos.tesoreria.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ViewModelTesoreria private constructor() {

    var  _message = MutableLiveData<String>()

    fun getMessage() : LiveData<String> {
        return  _message
    }

    companion object Singleton{
        private var instance:ViewModelTesoreria? = null

        fun getInstance():ViewModelTesoreria =
            instance ?: ViewModelTesoreria(

            ).also { instance = it }

        fun destroyInstance(){
            instance = null
        }
    }


}