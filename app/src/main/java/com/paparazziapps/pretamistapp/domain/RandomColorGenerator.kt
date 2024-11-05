package com.paparazziapps.pretamistapp.domain

import android.graphics.Color

object RandomColorGenerator {
    private val colors = listOf(
        Color.parseColor("#3F51B5"), // Indigo
        Color.parseColor("#673AB7"), // Deep Purple
        Color.parseColor("#4CAF50"), // Green
        Color.parseColor("#FFC107"), // Amber
        Color.parseColor("#FF9800"), // Orange
        Color.parseColor("#E91E63"), // Pink
        Color.parseColor("#9C27B0"), // Purple
        Color.parseColor("#00BCD4")  // Cyan
    )

    fun getRandomColor(): Int {
        return colors.random()
    }
}