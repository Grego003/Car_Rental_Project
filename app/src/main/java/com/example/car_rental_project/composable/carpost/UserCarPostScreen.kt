package com.example.car_rental_project.composable.carpost

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.car_rental_project.composable.home.CarCard
import com.example.car_rental_project.model.CarModel

@Composable
fun UserCarPostScreen(
    carList : List<CarModel>?,
    navigateToCarPostDetails : (id: String) -> Unit,
) {
    Column {
        Text(text = "Your Post")
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(carList != null) {
                items(items = carList) { car ->
                    CarCard(car) {
                        navigateToCarPostDetails(car.id ?: "")
                    }
                }
            }
        }
    }
}