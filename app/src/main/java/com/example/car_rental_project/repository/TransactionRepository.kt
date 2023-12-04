package com.example.car_rental_project.repository

import android.util.Log
import com.example.car_rental_project.dao.TransactionDao
import com.example.car_rental_project.model.CarModel
import com.example.car_rental_project.model.TransactionEntity
import com.example.car_rental_project.model.TransactionModel
import com.example.car_rental_project.model.TransactionStatus
import com.example.car_rental_project.service.FirebaseDBService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TransactionRepository(
    firebaseDb: FirebaseDBService,
) : TransactionDao {
    private val database = firebaseDb.getReferenceChild("transaction")
    override suspend fun createTransaction(
        transactionEntity: TransactionEntity,
    ): TransactionModel {
        val query = database.orderByChild("postCarId").equalTo(transactionEntity.postCarId)
        return try {
            val existingDataSnapshot = query.get().await()
            val matchingTransaction = existingDataSnapshot.children.find { snapshot ->
                snapshot.child("buyerId").getValue(String::class.java) == transactionEntity.buyerId
            }
            if (matchingTransaction != null) {
                return TransactionModel(data = null, errorMessage = "Transaction already exists.")
            }

            val transactionReference = database.push()
            val transactionData = transactionEntity.copy(
                id =transactionReference.key
            )
            transactionReference.setValue(transactionData).await()
            TransactionModel(data = transactionData, errorMessage = null)
        }catch (e : Exception) {
            TransactionModel(data = null, errorMessage = e.message)
        }
    }
    override fun getAllTransaction(): Flow<List<TransactionEntity>> {
        return callbackFlow {
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(transactionSnapshot: DataSnapshot) {
                    val transactionList = transactionSnapshot.children.mapNotNull { snapshot ->
                        snapshot.getValue(TransactionEntity::class.java)
                    }
                    try {
                        trySend(transactionList)
                    } catch (e: Exception) {
                        close(e)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    try {
                        close(error.toException())
                    } catch (e: Exception) {
                        Log.e("TransactionRepository", "Error while closing flow", e)
                    }
                }
            }
            database.addListenerForSingleValueEvent(valueEventListener)
            awaitClose {
                database.removeEventListener(valueEventListener)
            }
        }
    }

    override suspend fun updateTransaction(
        transactionStatus: TransactionStatus,
        currentTransaction: TransactionEntity
    ): TransactionModel
    {
        try {
            Log.d("CurrentTransaction", currentTransaction.id.toString())
            val transactionSnapshot = database.child(currentTransaction.id ?: "").get().await()
            if(transactionSnapshot.exists()) {
                val transactionRef = database.child(currentTransaction.id ?: "")
                val updatedTransaction = currentTransaction.copy(
                    status = transactionStatus

                )
                transactionRef.setValue(updatedTransaction).await()
                return TransactionModel(data = updatedTransaction, errorMessage = null)
            }
            else {
                return TransactionModel(data = null, errorMessage = "transaction not found")
            }
        } catch (e : Exception) {
            return TransactionModel(data = null, errorMessage = e.message)
        }
    }

}