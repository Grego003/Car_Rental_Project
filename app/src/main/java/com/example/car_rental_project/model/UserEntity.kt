package com.example.car_rental_project.model

data class UserEntity(
    val userId : String? = "",
    val username : String? = "",
    val email : String? = "",
    val profilePicture : String? = "",
    val phoneNumber : String? = "",
    val isVerified : Boolean? = false,
)


