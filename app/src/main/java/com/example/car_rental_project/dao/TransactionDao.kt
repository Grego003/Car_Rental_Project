package com.example.car_rental_project.dao

import android.content.ContentResolver
import android.net.Uri
import com.example.car_rental_project.model.CarModel
import com.example.car_rental_project.model.CarPostModel
import com.example.car_rental_project.model.TransactionEntity
import com.example.car_rental_project.model.TransactionModel
import com.example.car_rental_project.model.TransactionStatus
import com.example.car_rental_project.model.UserEntity
import kotlinx.coroutines.flow.Flow

interface TransactionDao {
    suspend fun createTransaction(
        transactionEntity: TransactionEntity,
        ) : TransactionModel
    fun getAllTransaction() : Flow<List<TransactionEntity>>
    suspend fun updateTransaction(
        transactionStatus: TransactionStatus,
        currentTransaction: TransactionEntity) : TransactionModel
    suspend fun deleteTransaction(
        transactionId : String
    ) : Boolean
}