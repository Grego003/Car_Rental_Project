package com.example.car_rental_project.state

import android.net.Uri

data class ProfileState (
    val email : String? = "",
    val userId : String? = "",
    val username : String? = "",
    val profilePicture : Uri? = null,
    val phoneNumber : String? = "",
    val money : Int? = 0,
    val premium : Boolean? = false,
    val isUpdateSuccessful : Boolean = false,
)