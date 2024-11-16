package com.paparazziapps.pretamistapp.presentation.profile.viewmodels

import androidx.lifecycle.ViewModel
import com.paparazziapps.pretamistapp.application.MyPreferences
import com.paparazziapps.pretamistapp.helper.replaceFirstCharInSequenceToUppercase

class ProfileViewModel(
    private val preferences: MyPreferences
) : ViewModel() {



    fun getEmail() = preferences.emailUser

    fun getBranchName() = preferences.branchName

    fun getCreationDate() = preferences.creationDate

    fun getIsActive() = preferences.isActiveUserString

    fun getLastnames() = preferences.lastnames

    fun getNames() = preferences.names

    fun getRole() = preferences.role

    fun getFullName():String {
        val namesWithUpperCase = replaceFirstCharInSequenceToUppercase(preferences.names)
        val lastnamesWithUpperCase = replaceFirstCharInSequenceToUppercase(preferences.lastnames)
        val fullName = "$namesWithUpperCase, $lastnamesWithUpperCase"
        return fullName
    }

}