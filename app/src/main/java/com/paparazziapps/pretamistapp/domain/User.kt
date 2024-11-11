package com.paparazziapps.pretamistapp.domain

data class User (
    var names: String? = null,
    var lastnames: String? = null,
    var branchId: Int? = null,
    var branch: String? = null,
    var email: String? = null,
    var password: String? = null,
    var activeUser: Boolean = false,
    var admin: Boolean = false,
    var superAdmin: Boolean = false,
    val dateCreated: String? = null,
)