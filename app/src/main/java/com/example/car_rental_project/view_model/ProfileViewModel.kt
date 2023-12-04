package com.example.car_rental_project.view_model

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.car_rental_project.model.CarPostModel
import com.example.car_rental_project.model.UserEntity
import com.example.car_rental_project.model.UserModel
import com.example.car_rental_project.service.FirebaseStorageService
import com.example.car_rental_project.state.AuthState
import com.example.car_rental_project.state.ProfileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel() : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    //Read Only state
    val state = _state.asStateFlow()

    fun onUpdateResult(result : UserModel) {
        _state.update { it.copy(
            isUpdateSuccessful = result.data != null,
        ) }
    }
    fun initializeOldData(currentUser : UserEntity) {
        _state.update { it.copy(
            email = currentUser.email,
            userId = currentUser.userId,
            username = currentUser.username,
            phoneNumber = currentUser.phoneNumber,
            premium = currentUser.premium,
        ) }
    }
    fun onUsernameChange(newUsername: String?) {
        val updatedUsername = newUsername ?: ""
        _state.update { it.copy(username = updatedUsername) }
    }

    fun onProfilePictureChange(image: Uri?) {
        _state.update { it.copy(profilePicture = image) }
    }
    fun onPhoneNumberChange(phoneNum : String?) {
        val updatedPhoneNumber = phoneNum ?: ""
        _state.update { it.copy(phoneNumber = updatedPhoneNumber) }
    }

    fun onPremiumChange(premium : Boolean?) {
        _state.update { it.copy(premium = premium!!) }
    }
    fun resetState() {
        _state.update { ProfileState() }
    }

}