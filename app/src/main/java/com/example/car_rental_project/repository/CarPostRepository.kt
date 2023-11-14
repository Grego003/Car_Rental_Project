package com.example.car_rental_project.repository

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.currentCompositionLocalContext
import com.example.car_rental_project.dao.CarPostDao
import com.example.car_rental_project.model.CarModel
import com.example.car_rental_project.model.CarPostModel
import com.example.car_rental_project.model.UserModel
import com.example.car_rental_project.service.FirebaseDBService
import com.example.car_rental_project.service.FirebaseStorageService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.tasks.await

class CarPostRepository(
    firebaseDb: FirebaseDBService,
    firebaseStorage : FirebaseStorageService,
    ) : CarPostDao {

    private val storage = firebaseStorage
    private val database = firebaseDb.getReferenceChild("carPost")
    override fun getAllCarPosts(): Flow<List<CarModel>> {
        return callbackFlow {
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(carSnapshot: DataSnapshot) {
                    val carList = carSnapshot.children.mapNotNull { snapshot ->
                        snapshot.getValue(CarModel::class.java)
                    }
                    try {
                        trySend(carList)
                    } catch (e: Exception) {
                        close(e)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    try {
                        close(error.toException())
                    } catch (e: Exception) {
                        Log.e("CarRepository", "Error while closing flow", e)
                    }
                }
            }
            database.addListenerForSingleValueEvent(valueEventListener)
            awaitClose {
                database.removeEventListener(valueEventListener)
            }
        }
    }
    override suspend fun getCarPostById(id: String): CarPostModel {
        return try {
            val dataSnapshot = database.child(id).get().await()
            val result = dataSnapshot.getValue(CarModel::class.java)
            CarPostModel(data = result, errorMessage = result?.let { null } ?: "Car id not found")
        } catch (e: Exception) {
            CarPostModel(data = null, errorMessage = e.message)
        }
    }
    override suspend fun createCarPost(
        userId : String?,
        carModel: CarModel,
        images: List<Uri>,
        contextResolver: ContentResolver,
    ) : CarPostModel {
        try {
            val carReference = database.push()
            Log.d("Images", images.toString())
            if (images.isNotEmpty()) {
                val imageUrls = images.map { imageUri ->
                    val image = storage.readImageBytes(contentResolver = contextResolver, uri = imageUri)
                    storage.uploadImageToFirebase(image)
                }
                val carData = carModel.copy(
                    id = carReference.key,
                    userId = userId,
                    images = imageUrls.filterNotNull()
                )
                carReference.setValue(carData).await()
                return CarPostModel(data = carData)
            } else {
                val carData = carModel.copy(id = carReference.key, userId = userId)
                carReference.setValue(carData).await()
                return CarPostModel(data = carData)
            }
        } catch (e: Exception) {
            Log.e("CarRepository", "Error in createCarPost", e)
            return CarPostModel(data = null, errorMessage = e.message)
        }
    }


    override suspend fun deleteCarPost(id: String): Boolean {
        return try {
            val carReference = database.child(id)
            carReference.removeValue().await()
            true
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun editCarPost(id: String, updatedCar: CarModel): CarModel {
         try {
            val carReference = database.child(id)
            val carData = updatedCar.copy(id = id)
            carReference.setValue(carData).await()
             return carData
        } catch (e: Exception) {
            throw e
        }
    }
}