package com.example.car_rental_project.composable.carpost

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.car_rental_project.model.UserEntity

@Composable
fun CarPostDetailScreen(
    authUser : UserEntity?,
    navController: NavController,
    carData: CarModel,
    createTransaction : () -> Unit,
    ) {
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
                .background(color = Color.Blue)
        ) {
            LazyRow {
                items(carData.images ?: emptyList()) { image ->
                    AsyncImage(model = image,
                        contentDescription = null,
                        modifier = Modifier
                            .width(200.dp)
                            .height(200.dp),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Car Details
        CarDetails(carData, createTransaction, authUser)
    }
}

@Composable
fun CarDetails(carData: CarModel, createTransaction: () -> Unit, authUser : UserEntity?) {
    LazyColumn {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = carData.brand ?: "")
                Text(text = carData.model ?: "")

//                Text(text=carData.model ?: "NO MODEL")
////                CarDetailItem(Icons.Default.Info, carData.model ?: "")
////                CarDetailItem(Icons.Default.Info, carData.yearBought.toString() ?: "")
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
            CarDetailItem(Icons.Default.Info, "Year Bought", carData.yearBought ?: "")
            if(carData.userId != authUser?.userId) {
                ButtonList(createTransaction = createTransaction)
            }
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
fun ButtonList(createTransaction : ()-> Unit) {
    var isDialogVisible by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter)
        ) {
            Button(
                onClick = { isDialogVisible = true },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Text("Buy Now")
            }

            Button(
                onClick = { /* Handle button click */ },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Text("WhatsApp")
            }

            if (isDialogVisible) {
                BuyConfirmationDialog(
                    onConfirm = {
                        createTransaction.invoke()
                        isDialogVisible = false
                    },
                    onCancel = { isDialogVisible = false }
                )
            }

        }
    }
}



@Composable
fun BuyConfirmationDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text("Are you sure you want to buy?")
        },
        confirmButton = {
            Button(
                onClick = onConfirm
            ) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(
                onClick = onCancel
            ) {
                Text("Cancel")
            }
        }
    )
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