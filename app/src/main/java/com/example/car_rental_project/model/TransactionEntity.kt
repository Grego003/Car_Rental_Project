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
    val title : String? = "",
    val brand: String? = "",
    val model: String? = "",
    val price: Int? = 0,
    val status : TransactionStatus ?= TransactionStatus.WAITING,
)