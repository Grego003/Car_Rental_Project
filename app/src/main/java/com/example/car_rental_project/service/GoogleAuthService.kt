package com.example.car_rental_project.service

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.core.content.ContextCompat.getString
import com.example.car_rental_project.R
import com.example.car_rental_project.model.UserModel
import com.example.car_rental_project.model.UserEntity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.firebase.Firebase
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.database.getValue
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class GoogleAuthService(
    private val context: Context,
    private val oneTapClient : SignInClient,
    private val firebaseService :  FirebaseDBService) {

    private val auth = Firebase.auth
    private val userDatabase = firebaseService.getReferenceChild("users")

    suspend fun signInWithGoogleOneTapClient() : IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        }catch (e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun createUserWithEmailAndPassword(email: String?, password: String?, username: String?): UserModel {
        if (email != null && password != null && username != null) {
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user
                if (user != null) {
                    val profileUpdate = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()
                    user.updateProfile(profileUpdate).await()
                    val userData = UserEntity(
                    userId = user.uid,
                    username = user.displayName,
                    email = user.email,
                    )
                    userDatabase.child(user.uid).setValue(userData)
                    return UserModel(
                        userData,
                        errorMessage = null
                    )
                } else {
                    return UserModel(data = null, errorMessage = "User creation failed")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is CancellationException) throw e
                return UserModel(data = null, errorMessage = e.message)
            }
        } else {
            return UserModel(data = null, errorMessage = getString(context, R.string.sign_up_Empty))
        }
    }

    suspend fun signInUserWithEmailAndPassword(email: String?, password: String?) : UserModel {
        if (email != null && password != null) {
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val user = authResult.user ?: throw Exception("FirebaseUser is null")
                val userSnapshot = userDatabase.child(user.uid).get().await()
                return if (userSnapshot.exists()) {
                    val userEntity = userSnapshot.getValue<UserEntity>()
                    if (userEntity != null) {
                        UserModel(data = userEntity, errorMessage = null)
                    } else {
                        UserModel(data = null, errorMessage = "User data not found")
                    }
                } else {
                    UserModel(data = null, errorMessage = "User does not exist")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is CancellationException) throw e
                return UserModel(data = null, errorMessage = e.message)
            }
        } else {
            return UserModel(data = null, errorMessage = getString(context, R.string.sign_in_Empty))
        }
    }
    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        }catch (e: Exception){
            e.printStackTrace()
            if(e is CancellationException) throw e
        }
    }

    /// TODO: make the error handling ty gua males
    suspend fun getSignedInUser(): UserEntity? {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            try {
                val userSnapshot = userDatabase.child(currentUser.uid).get().await()
                Log.d("USERSNAPSHOT", userSnapshot.toString())
                if (userSnapshot.exists()) {
                    return userSnapshot.getValue<UserEntity>()
                }
            }catch (e: Exception) {
                e.printStackTrace()
                throw Exception(e)
            }
        }
        return null
    }

    suspend fun signInWithIntent(intent: Intent) : UserModel {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {
            val user = auth.signInWithCredential(googleCredential).await().user ?: throw Exception("User not Found")
            val userSnapshot = userDatabase.child(user.uid).get().await()
            if (userSnapshot.exists()) {
                val userData = userSnapshot.getValue<UserEntity>()
                return UserModel(data = userData, errorMessage = null)
            }
            else {
                val userData  = UserEntity(
                    userId = user.uid,
                    username = user.displayName,
                    email = user.email,
                    profilePicture = user.photoUrl?.toString(),
                )
                userDatabase.child(user.uid).setValue(userData)
                return UserModel(
                    data = userData,
                    errorMessage = null
                )
            }
        }catch (e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
            UserModel(
                data = null,
                errorMessage = e.message
            )
        }
    }

    private fun buildSignInRequest() : BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}