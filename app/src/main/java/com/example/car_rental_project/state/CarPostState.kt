package com.example.car_rental_project.state

import android.net.Uri
import com.example.car_rental_project.model.CarCategory
import com.example.car_rental_project.model.CarCondition
import com.example.car_rental_project.model.CarModel
import com.example.car_rental_project.model.EngineCapasity
import com.example.car_rental_project.model.FuelType
import java.time.Year

data class CarPostState(
    val title : String? = "",
    val brand: String? = "",
    val model: String? = "",
    val condition : CarCondition? = CarCondition.BEKAS,
    val yearBought : Year? = try {
        Year.now()
    } catch (e: Exception) {
        null
    },
    val fuelType : FuelType? = FuelType.BENSIN,
    val odometer : String ? = "",
    val category : CarCategory ? = CarCategory.CLASSIC_CAR,
    val images : List<Uri>? = emptyList(),
    val engineCapasity : EngineCapasity ?= EngineCapasity.CC_1000_TO_1500,
    val description : String? = "",
    val price : String? = "",
    val legalRequirements : Boolean ? = false,
    val isCreatePostSuccessful : Boolean = false,
    val errorMessage : String?= "",
    val isLoading : Boolean ? = false,
)