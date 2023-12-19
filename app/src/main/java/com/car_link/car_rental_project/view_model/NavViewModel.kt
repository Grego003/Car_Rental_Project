package com.car_link.car_rental_project.view_model

import androidx.lifecycle.ViewModel
import com.car_link.car_rental_project.state.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NavViewModel : ViewModel() {
    private val _state = MutableStateFlow(0)
    val state = _state.asStateFlow()

    fun updateNav(navIndex: Int) {
        _state.value = navIndex
    }

    fun resetState() {
        _state.value = 0
    }
}