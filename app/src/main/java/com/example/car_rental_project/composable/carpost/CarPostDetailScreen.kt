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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Phone
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.car_rental_project.composable.home.formatPrice
import com.example.car_rental_project.model.UserEntity
import java.text.NumberFormat
import java.util.Locale

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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    navController.popBackStack()
                },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = carData.title ?: "",
                style = MaterialTheme.typography.titleLarge,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.onBackground)
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
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = carData.brand ?: "", style = TextStyle(Color.Black, fontSize = 24.sp).copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = carData.model ?: "", style = TextStyle(Color.Black, fontSize = 24.sp).copy(fontWeight = FontWeight.Medium))
                }
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Rp ${formatPrice(carData.price)}",
                        style = MaterialTheme.typography.titleMedium
                            .copy(fontWeight = FontWeight.Bold)
                            .copy(Color.White),
                        modifier = Modifier
                            .padding(8.dp)
                    )
                }
            }
        }
        item {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(),
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
                text = "Specifications",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )

            CarDetailItem(Icons.Outlined.Info, "Category: ", carData.category?.displayName ?: "")
            CarDetailItem(Icons.Outlined.Info, "Condition: ", carData.condition?.displayName ?: "")
            CarDetailItem(Icons.Outlined.Info, "Fuel Type: ", carData.fuelType?.displayName ?: "")
            CarDetailItem(Icons.Outlined.Info, "Engine Capacity: ", carData.engineCapasity?.displayName ?: "")
            CarDetailItem(Icons.Outlined.Info, "Odometer: ", "${carData.odometer} km")
            CarDetailItem(Icons.Outlined.Info, "Year Bought: ", carData.yearBought ?: "")
        }

        item{
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
fun ButtonList(createTransaction: () -> Unit) {
    var isDialogVisible by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FloatingActionButton(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth(),
                onClick = { isDialogVisible = true },
            ) {
                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null)
            }
            Text("Buy Now", style = MaterialTheme.typography.titleMedium)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FloatingActionButton(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth(),
                onClick = { /* Ayo Grego Whatsapp :) */ },
            ) {
                Icon(imageVector = Icons.Default.Phone, contentDescription = null)
            }
            Text("WhatsApp", style = MaterialTheme.typography.titleMedium)
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
            OutlinedButton(
                onClick = onCancel
            ) {
                Text(
                    text = "Cancel",
                    color = Color.Black,
                )
            }
        }
    )
}
