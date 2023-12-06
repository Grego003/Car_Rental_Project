package com.example.car_rental_project.dao

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import com.example.car_rental_project.model.CarModel
import com.example.car_rental_project.model.CarPostModel
import com.example.car_rental_project.model.UserEntity
import kotlinx.coroutines.flow.Flow

interface CarPostDao {
    fun getAllCarPosts(): Flow<List<CarModel>>
    suspend fun getCarPostById(id : String) : CarPostModel
    suspend fun createCarPost(userId: String?,
                              sellerName : String?,
                              carModel : CarModel,
                              images: List<Uri> = emptyList(),
                              contextResolver: ContentResolver
    ) : CarPostModel
    suspend fun getUserCarPosts(user : UserEntity?) : Flow<List<CarModel>>?
    suspend fun deleteCarPost(id : String) : Boolean
    suspend fun editCarPost(id : String, updatedCar: CarModel) : CarModel
}