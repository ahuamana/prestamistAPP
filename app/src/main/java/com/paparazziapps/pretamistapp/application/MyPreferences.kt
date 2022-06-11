package com.paparazziteam.yakulap.helper.applicacion

import android.graphics.Color

class MyPreferences {

    val prefs = CacheData()

    //PREFERENCES STRING
    var email_login: String
        get()      = prefs.getString("email_login", "")
        set(value) = prefs.setString("email_login", value)




    //PREFERENCES BOOLEAN
    var isLogin: Boolean
        get()      = prefs.getBoolean("isLogin", false)
        set(value) = prefs.setBoolean("isLogin", value)

    var isActiveUser: Boolean
        get()      = prefs.getBoolean("isActiveUser", false)
        set(value) = prefs.setBoolean("isActiveUser", value)


    //PREFERENCES INTEGER
    var color: Int
        get() = prefs.getInt("color",  Color.parseColor("#ff0066"))
        set(value) = prefs.setInt("color", value)
}