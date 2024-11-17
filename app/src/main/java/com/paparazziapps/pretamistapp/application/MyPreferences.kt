package com.paparazziapps.pretamistapp.application

import android.content.Context
import android.graphics.Color
import com.paparazziapps.pretamistapp.helper.INT_DEFAULT

class MyPreferences(private val context: Context) {

    private val prefs = CacheData(context)

    //PREFERENCES STRING
    var emailUser: String
        get()      = prefs.getString("emailUser", "")
        set(value) = prefs.setString("emailUser", value)


    var branchName: String
        get()      = prefs.getString("branchName", "")
        set(value) = prefs.setString("branchName", value)

    //Sucursales All
    var branches: String
    get()      = prefs.getString("branches", "")
    set(value) = prefs.setString("branches", value)


    var names: String
    get()      = prefs.getString("names", "")
    set(value) = prefs.setString("names", value)

    // lastnames
    var lastnames: String
    get()      = prefs.getString("lastnames", "")
    set(value) = prefs.setString("lastnames", value)

    //role
    var role: String
    get()      = prefs.getString("role", "")
    set(value) = prefs.setString("role", value)

    //creationDate
    var creationDate: Long
    get()      = prefs.getLong("creationDate", 0)
    set(value) = prefs.setLong("creationDate", value)

    //isActive
    var isActive: String
    get()      = prefs.getString("isActive", "")
    set(value) = prefs.setString("isActive", value)

    var savedEmail: String
    get()      = prefs.getString("savedEmail", "")
    set(value) = prefs.setString("savedEmail", value)


    //PREFERENCES BOOLEAN
    var isLogin: Boolean
        get()      = prefs.getBoolean("isLogin", false)
        set(value) = prefs.setBoolean("isLogin", value)

    ////Logica de datos de registro de datos
    var isAdmin: Boolean
        get()      = prefs.getBoolean("isAdmin", false)
        set(value) = prefs.setBoolean("isAdmin", value)

    var isSuperAdmin: Boolean
        get()      = prefs.getBoolean("isSuperAdmin", false)
        set(value) = prefs.setBoolean("isSuperAdmin", value)

    var isActiveUser: Boolean
        get()      = prefs.getBoolean("isActiveUser", false)
        set(value) = prefs.setBoolean("isActiveUser", value)

    val isActiveUserString: String
        get() = if(isActiveUser) "Activo" else "Inactivo"


    //PREFERENCES INTEGER
    var color: Int
        get() = prefs.getInt("color",  Color.parseColor("#ff0066"))
        set(value) = prefs.setInt("color", value)

    var branchId: Int
        get()      = prefs.getInt("branchId", INT_DEFAULT)
        set(value) = prefs.setInt("branchId", value)


    fun removeLoginData(){
        prefs.remove("isLogin")
        prefs.remove("emailUser")
        prefs.remove("branchName")
        prefs.remove("isAdmin")
        prefs.remove("isSuperAdmin")
        prefs.remove("isActiveUser")
        prefs.remove("branchId")
    }

    fun setEmail(email: String){
        emailUser = email
    }

    fun getEmail(): String{
        return emailUser
    }



}