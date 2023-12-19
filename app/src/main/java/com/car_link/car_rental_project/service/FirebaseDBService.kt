package com.car_link.car_rental_project.service

import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

object FirebaseDBService {
    private val databaseRef: DatabaseReference by lazy {
          Firebase.database.reference
        }
    fun getReferenceChild(key : String): DatabaseReference {
        return databaseRef.child(key)
    }
}