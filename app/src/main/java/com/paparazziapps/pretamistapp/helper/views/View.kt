package com.paparazziapps.pretamistapp.helper.views

import android.view.View
import com.paparazziapps.pretamistapp.helper.SHORT_ANIMATION_DURATION

fun View.beInvisible() {
    visibility = View.INVISIBLE
}

fun View.beVisible() {
    visibility = View.VISIBLE
}

fun View.beGone() {
    visibility = View.GONE
}


fun View.isVisible() = visibility == View.VISIBLE

fun View.isInvisible() = visibility == View.INVISIBLE

fun View.isGone() = visibility == View.GONE


fun View.fadeIn() {
    animate().alpha(1f).setDuration(SHORT_ANIMATION_DURATION).withStartAction { beVisible() }.start()
}

fun View.fadeOut() {
    animate().alpha(0f).setDuration(SHORT_ANIMATION_DURATION).withEndAction { beGone() }.start()
}