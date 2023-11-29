package com.example.car_rental_project.model

import java.time.Year
import com.google.gson.annotations.SerializedName
enum class FuelType(@SerializedName("fuelType") val displayName: String) {
    BENSIN("Bensin"),
    DIESEL("Diesel"),
    HYBRID("Hybrid"),
    LISTRIK("Listrik"),
}
enum class CarCondition(@SerializedName("condition") val displayName: String) {
    BARU("Baru"),
    BEKAS("Bekas"),
}
enum class CarCategory(@SerializedName("category") val displayName: String) {
    BUS("Bus"),
    CLASSIC_CAR("Classic Car"),
    COMPACT_CITY_CAR("Compact & City Car"),
    CONVERTIBLE("Convertible"),
    COUPE("Coupe"),
    DOUBLE_CABIN("Double Cabin"),
    HATCHBACK("Hatchback"),
    JEEP("Jeep"),
    MPV("MPV"),
    MINIBUS("Minibus"),
    OFFROAD("Offroad"),
    PICKUP("Pick-up"),
    SUV("SUV"),
    SEDAN("Sedan"),
    SPORTS_SUPER_CAR("Sports & Super Car"),
    TRUCK("Truck"),
    VAN("Van"),
    WAGON("Wagon")
}
enum class EngineCapasity(@SerializedName("engineCapasity") val displayName: String) {
    LESS_THAN_1000CC("<1000cc"),
    CC_1000_TO_1500(">1000cc - 1500cc"),
    CC_1500_TO_2000(">1500cc - 2000cc"),
    CC_2000_TO_3000(">2000cc - 3000cc"),
    MORE_THAN_3000CC(">3000cc"),
}
data class CarModel(
    val id: String? = "",
    val userId : String ? = "",
    val title : String? = "",
    val brand: String? = "",
    val model: String? = "",
    val condition : CarCondition? = CarCondition.BEKAS,
    val yearBought : String? = try {
        Year.now().toString()
    } catch (e: Exception) {
        null
    },
    val fuelType : FuelType? = FuelType.BENSIN,
    val odometer : Int? = 0,
    val category : CarCategory? = CarCategory.CLASSIC_CAR,
    val images : List<String>? = listOf(""),
    val engineCapasity : EngineCapasity? = EngineCapasity.CC_1000_TO_1500,
    val description : String? = "",
    val price : Int? = 0,
    val isPremiumPost : Boolean? = false,
    val legalRequirements : Boolean ? = false,
)


