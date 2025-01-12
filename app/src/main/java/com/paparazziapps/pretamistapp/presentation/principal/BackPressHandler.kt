package com.paparazziapps.pretamistapp.presentation.principal

import android.content.Context
import android.widget.Toast

class BackPressHandler(
    private val context: Context
) {

    private var lastBackPressTime: Long = 0
    private var BACK_PRESS_INTERVAL = 2000 // 2 seconds

    fun handleOnBackPressed(
        onBackPressed: () -> Unit
    ) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBackPressTime < BACK_PRESS_INTERVAL) {
            onBackPressed()
        } else {
            lastBackPressTime = currentTime
            Toast.makeText(context, "Presiona de nuevo para salir", Toast.LENGTH_SHORT).show()
        }
    }


}