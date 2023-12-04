package com.example.car_rental_project.composable.carpost

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.car_rental_project.composable.home.CarCard
import com.example.car_rental_project.model.CarModel
import com.example.car_rental_project.repository.CarPostRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
@Composable
fun UserCarPostScreen(
    carList : List<CarModel>?,
    navigateToCarPostDetails : (id: String) -> Unit,
    deletePost : (id : String) -> Unit,
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
                    Column {
                        Box {
                            CloseButton {
                                deletePost(car.id ?: "")
                            }
                            CarCard(car) {
                                navigateToCarPostDetails(car.id ?: "")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CloseButton(onClick: () -> Unit) {
    // You can customize the appearance of the "X" button as needed
    Icon(
        imageVector = Icons.Filled.Delete,
        contentDescription = "Close",
        tint = Color.Black, // Customize the color
        modifier = Modifier
            .padding(16.dp)
            .size(35.dp)
            .clickable { onClick() }
            .zIndex(2.0f)
    )
}