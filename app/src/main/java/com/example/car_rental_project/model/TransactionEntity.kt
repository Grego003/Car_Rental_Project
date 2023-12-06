package com.example.car_rental_project.model

import com.google.gson.annotations.SerializedName


enum class TransactionStatus(@SerializedName("transactionStatus") val displayName: String) {
    FINISHED("Bensin"),
    CANCELLED("Diesel"),
    WAITING("Hybrid"),
}

data class TransactionEntity(
    val id: String? = "",
    val postCarId : String? = "",
    val buyerId : String ? = "",
    val sellerId : String ? = "",
    val buyerName : String ? = "",
    val sellerName : String ? = "",
    val carPost : CarModel ? = null,
    val status : TransactionStatus ?= TransactionStatus.WAITING,
)