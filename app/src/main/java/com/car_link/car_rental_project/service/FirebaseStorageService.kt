package com.car_link.car_rental_project.service

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID

object FirebaseStorageService {
    private val storageRef: StorageReference by lazy {
        Firebase.storage.reference
    }
    data class ImageData(val data: ByteArray, val extension: String?)

    fun readImageBytes(contentResolver: ContentResolver, uri: Uri): ImageData {
        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = ByteArrayOutputStream()

        inputStream?.use { input ->
            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
        }

        val imageData = ImageData(outputStream.toByteArray(), getFileExtension(contentResolver, uri))
        outputStream.close()
        return imageData
    }

    private fun getFileExtension(contentResolver: ContentResolver, uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                val filePath = it.getString(it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                return filePath.substring(filePath.lastIndexOf('.') + 1)
            }
        }

        return null
    }
    suspend fun uploadImageToFirebase(storage : String?, imageData : ImageData): String? {
        try {
            val fileName = "${UUID.randomUUID()}.${imageData.extension}"
            val refStorage = storageRef.child("$storage/$fileName")
            val uploadTask = refStorage.putBytes(imageData.data).await()
            if (uploadTask.task.isSuccessful) {
                return refStorage.downloadUrl.await().toString()
            } else {
                Log.e("FirebaseStorageService", "Upload failed")
            }
        } catch (e: Exception) {
            Log.e("FirebaseStorageService", "Error in uploadImageToFirebase", e)
        }
        return null // Add this line to handle the case where the upload fails
    }
}