package com.example.car_rental_project.service

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import com.example.car_rental_project.R
import com.example.car_rental_project.model.AuthModel
import com.example.car_rental_project.model.UserModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.firebase.Firebase
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.SignInMethodQueryResult
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class GoogleAuthService(
    private val context: Context,
    private val oneTapClient : SignInClient ) {
    private val auth = Firebase.auth

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

    suspend fun createUserWithEmailAndPassword(email: String?, password: String?, username: String?): AuthModel {
        if (email != null && password != null && username != null) {
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user
                if (user != null) {
                    val profileUpdate = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()

                    user.updateProfile(profileUpdate).await()

                    return AuthModel(
                        data = UserModel(
                            userId = user.uid,
                            username = user.displayName,
                            profilePicture = user.photoUrl?.toString()
                        ),
                        errorMessage = null
                    )
                } else {
                    return AuthModel(data = null, errorMessage = "User creation failed")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is CancellationException) throw e
                return AuthModel(data = null, errorMessage = e.message)
            }
        } else {
            return AuthModel(data = null, errorMessage = "Email, password, and username must not be null")
        }
    }

    suspend fun signInUserWithEmailAndPassword(email: String?, password: String?) : AuthModel {
        if (email != null && password != null) {
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val user = authResult.user
                return AuthModel(
                    data = user?.run {
                        UserModel(
                            userId = uid,
                            username = displayName,
                            profilePicture = photoUrl?.toString()
                        )
                    },
                    errorMessage = null
                )
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is CancellationException) throw e
                return AuthModel(data = null, errorMessage = e.message)
            }
        } else {
            return AuthModel(data = null, errorMessage = "Email and password must not be null")
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

    fun getSignedInUser() : UserModel? = auth.currentUser?.run {
        UserModel(
            userId = uid,
            username = displayName,
            profilePicture = photoUrl?.toString()
        )
    }

    suspend fun signInWithIntent(intent: Intent) : AuthModel {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {
            val user = auth.signInWithCredential(googleCredential).await().user
            AuthModel(
                data = user?.run {
                    UserModel(
                        userId = uid,
                        username = displayName,
                        profilePicture = photoUrl?.toString()
                    )
                },
                errorMessage = null
            )
        }catch (e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
            AuthModel(
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