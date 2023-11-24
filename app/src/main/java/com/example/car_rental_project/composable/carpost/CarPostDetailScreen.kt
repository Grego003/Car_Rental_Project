package com.example.car_rental_project.composable.carpost

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.car_rental_project.model.CarModel
import androidx.compose.material3.MaterialTheme
import coil.compose.rememberAsyncImagePainter

@Composable
fun CarPostDetailScreen(navController: NavController, carData: CarModel) {
    BackHandler {
        navController.popBackStack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Car Title
        Text(
            text = carData.title ?: "",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Car Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .shadow(16.dp, shape = MaterialTheme.shapes.medium)
        ) {
            carData.images?.firstOrNull()?.let { coverImage ->
                Image(
                    painter = rememberAsyncImagePainter(coverImage),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Car Details
        CarDetails(carData)
    }
}

@Composable
fun CarDetails(carData: CarModel) {
    LazyColumn {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CarDetailItem(Icons.Default.Info, carData.brand ?: "")
                CarDetailItem(Icons.Default.Info, carData.model ?: "")
                CarDetailItem(Icons.Default.Info, carData.yearBought ?: "")
            }
        }
        item {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color.Gray
            )
        }
        item {
            Text(
                text = "Description",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = carData.description ?: "",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(8.dp)
            )
        }
        item {
            // Divider
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color.Gray
            )
        }
        item {
            // Additional Details
            Text(
                text = "Additional Details",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
            CarDetailItem(Icons.Default.Info, "Category", carData.category?.displayName ?: "")
            CarDetailItem(Icons.Default.Info, "Condition", carData.condition?.displayName ?: "")
            CarDetailItem(Icons.Default.Info, "Fuel Type", carData.fuelType?.displayName ?: "")
            CarDetailItem(Icons.Default.Info, "Engine Capacity", carData.engineCapasity?.displayName ?: "")
            CarDetailItem(Icons.Default.Info, "Odometer", "${carData.odometer} km")
            CarDetailItem(Icons.Default.Info, "Price", "$${carData.price}")
        }
    }
}

@Composable
fun CarDetailItem(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = value, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun CarDetailItem(icon: ImageVector, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = value, style = MaterialTheme.typography.bodySmall)
    }
}