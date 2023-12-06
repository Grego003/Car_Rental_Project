package com.example.car_rental_project.model

import java.math.BigInteger

data class UserEntity(
    val userId : String? = "",
    val username : String? = "",
    val email : String? = "",
    val profilePicture : String? = "",
    val phoneNumber : String? = "",
    val money : Long? = 0,
    val premium : Boolean? = false,
)


