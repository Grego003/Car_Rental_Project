package com.example.car_rental_project.composable.carpost

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.car_rental_project.model.CarModel

@Composable
fun CarPostDetailScreen(carData : CarModel) {
    Column {
        Text(text = carData.title ?: "")
        Text(text = carData.brand ?: "")
        Text(text = carData.model ?: "")
        Text(text = carData.description ?: "")
        Text(text = carData.category?.displayName ?: "")
        Text(text = carData.condition?.displayName ?: "")
        Text(text = carData.yearBought ?: "")
        Text(text = carData.engineCapasity?.displayName ?: "")
        Text(text = carData.fuelType?.displayName ?: "")
        LazyColumn {
            items(carData.images ?: emptyList()){ image ->
                AsyncImage(model = image, contentDescription = null, modifier = Modifier.size(100.dp))
            }
        }
        Text(text = carData.odometer.toString())
        Text(text = carData.price.toString())
    }

}

@Preview(showBackground = true)
@Composable
fun CarPostDetailScreenPreview() {
    CarPostDetailScreen(carData = CarModel())
}

