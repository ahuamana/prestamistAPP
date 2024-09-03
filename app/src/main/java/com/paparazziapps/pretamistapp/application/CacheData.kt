package com.paparazziapps.pretamistapp.application

import android.content.Context
import android.content.SharedPreferences
import com.paparazziapps.pretamistapp.helper.application

class CacheData constructor(name: String? = "Yaku_lab_preferences_common") {

    private val sharedPreferences: SharedPreferences = application.getSharedPreferences(name, Context.MODE_PRIVATE)

    fun hasKey(key: String): Boolean = sharedPreferences.contains(key)

    fun clear(){
        sharedPreferences.edit().clear()?.apply()
    }

    fun remove(key: String) {
        if(hasKey(key))
            sharedPreferences.edit().remove(key)?.apply()
    }

    fun setInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value)?.apply()
    }
    fun getInt(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }
    fun getInt(key: String): Int? {
        return if(hasKey(key)){
            sharedPreferences.getInt(key, 0)
        } else {
            null
        }
    }

    fun setFloat(key: String, value: Float) {
        sharedPreferences.edit().putFloat(key, value)?.apply()
    }
    fun getFloat(key: String, defaultValue: Float): Float {
        return sharedPreferences.getFloat(key, defaultValue)
    }
    fun getFloat(key: String): Float? {
        return if(hasKey(key)){
            sharedPreferences.getFloat(key, 0f)
        } else {
            null
        }
    }

    fun setLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value)?.apply()
    }
    fun getLong(key: String, defaultValue: Long): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }
    fun getLong(key: String): Long? {
        return if(hasKey(key)){
            sharedPreferences.getLong(key, 0)
        } else {
            null
        }
    }

    fun setString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value)?.apply()
    }
    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }
    fun getString(key: String): String? {
        return if(hasKey(key)){
            sharedPreferences.getString(key, "")
        } else {
            null
        }
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }
    fun getBoolean(key: String): Boolean? {
        return if(hasKey(key)){
            sharedPreferences.getBoolean(key, false)
        } else {
            null
        }
    }
    fun setBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value)?.apply()
    }
}