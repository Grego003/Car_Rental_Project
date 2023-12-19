package com.car_link.car_rental_project.dao

import android.content.ContentResolver
import android.net.Uri
import com.car_link.car_rental_project.model.CarModel
import com.car_link.car_rental_project.model.CarPostModel
import com.car_link.car_rental_project.model.TransactionEntity
import com.car_link.car_rental_project.model.TransactionModel
import com.car_link.car_rental_project.model.TransactionStatus
import com.car_link.car_rental_project.model.UserEntity
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