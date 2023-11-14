package com.example.car_rental_project.view_model

import androidx.lifecycle.ViewModel
import com.example.car_rental_project.model.UserModel
import com.example.car_rental_project.state.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    //Read Only state
    val state = _state.asStateFlow()

    fun onSignInResult(result : UserModel) {
        _state.update { it.copy(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage
        ) }
    }
    fun onEmailChange(newEmail: String?) {
        val updatedEmail = newEmail ?: ""
        _state.update { it.copy(email = updatedEmail) }
    }

    fun onPasswordChange(newPassword: String?) {
        val updatedPassword = newPassword ?: ""
        _state.update { it.copy(password = updatedPassword) }
    }

    fun onUsernameChange(newUsername : String?) {
        val updatedUsername = newUsername ?: ""
        _state.update { it.copy(username = updatedUsername) }
    }

    fun resetState() {
        _state.update { AuthState() }
    }
}