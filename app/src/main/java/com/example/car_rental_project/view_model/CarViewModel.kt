package com.example.car_rental_project.view_model

import android.net.Uri
import androidx.compose.runtime.internal.isLiveLiteralsEnabled
import androidx.lifecycle.ViewModel
import com.example.car_rental_project.model.CarCategory
import com.example.car_rental_project.model.CarCondition
import com.example.car_rental_project.model.CarPostModel
import com.example.car_rental_project.model.EngineCapasity
import com.example.car_rental_project.model.FuelType
import com.example.car_rental_project.state.AuthState
import com.example.car_rental_project.state.CarPostState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.Year

class CarViewModel() : ViewModel() {
    private val _state = MutableStateFlow(CarPostState())
    val state = _state.asStateFlow()

    fun onCreateLoad() {
        _state.update { it.copy(
            isLoading = true
        ) }
    }
    fun OnLoadFinished() {
        _state.update { it.copy(
            isLoading = false
        ) }
    }

    fun onCreateResult(result : CarPostModel) {
        _state.update { it.copy(
            isLoading = false,
            isCreatePostSuccessful = result.data != null,
            errorMessage = result.errorMessage
        ) }
    }
    fun onTitleChange(title : String?) {
        val updatedTitle = title ?: ""
        _state.update { it.copy(title = updatedTitle) }
    }
    fun onBrandChange(brand : String?) {
        val updatedBrand = brand ?: ""
        _state.update { it.copy(brand = updatedBrand) }
    }
    fun onModelChange(model : String?) {
        val updatedModel = model ?: ""
        _state.update { it.copy(model = updatedModel) }
    }
    fun onConditionChange(condition : CarCondition) {
        _state.update { it.copy(condition = condition) }
    }
    fun onYearBoughtChange(year : Year?) {
        val updatedYear = year ?: Year.now()
        _state.update { it.copy(yearBought = year) }
    }

    fun onFuelTypeChange(fuelType : FuelType) {
        _state.update { it.copy(fuelType = fuelType) }
    }
    fun onOdometerChange(odometer: String?) {
        val updatedOdometer = odometer ?: "0"
        _state.update { it.copy(odometer = updatedOdometer) }
    }
    fun onImagesChange(images: List<Uri>?) {
        val updatedImages = images ?: emptyList()
        _state.update { it.copy(images = updatedImages) }
    }

    fun onCategoryChange(category : CarCategory) {
        _state.update { it.copy(category = category) }
    }
    fun onEngineCapasityChange(engineCapasity : EngineCapasity) {
        _state.update { it.copy(engineCapasity = engineCapasity) }
    }
    fun onDescriptionChange(description : String?) {
        val updatedDescription = description ?: ""
        _state.update { it.copy(description = updatedDescription) }
    }
    fun onPriceChange(price: String?) {
        val updatedPrice = price ?: "0"
        _state.update { it.copy(price = updatedPrice) }
    }
    fun onLegalRequirementsChange(legalRequirements : Boolean?) {
        val updatedLegalRequirements = legalRequirements ?: false
        _state.update { it.copy(legalRequirements = updatedLegalRequirements) }
    }
    fun resetState() {
        _state.update { CarPostState() }
    }
}
