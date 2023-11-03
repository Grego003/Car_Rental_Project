package com.example.car_rental_project.state

data class AuthState(
    var email : String? = null,
    var password : String? = null,
    var username : String? = null,
    val isSignInSuccessful : Boolean = false,
    val signInError : String? = null,
)