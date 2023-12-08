package com.example.car_rental_project.composable.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.car_rental_project.R
import com.example.car_rental_project.model.CarModel
import com.example.car_rental_project.model.UserEntity

//Todo Rapihin ama buat design nya (dipecah pecah kaya login)
@Composable
fun HomeScreen(
    userData: UserEntity?,
    onSignOut: () -> Unit,
    carList: List<CarModel>,
    navigateToCreateCarPost: () -> Unit,
    navigateToCarPostDetails: (carId: String) -> Unit,
    navigateToProfile : ()->Unit,
) {
    var isDialogVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.LightGray)
                .padding(16.dp),
            horizontalArrangement = Arrangement
                .spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = userData?.username ?: "",
                style = MaterialTheme.typography.titleLarge
            )
            AsyncImage(
                model = userData?.profilePicture?.let { if (it.isEmpty()) null else it },
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.profile_placeholder),
                fallback = painterResource(id = R.drawable.profile_placeholder),
            )
        }
        TextField(
            value = "",
            onValueChange = { /* Handle search input change */ },
            modifier = Modifier
                .weight(0.15f)
                .fillMaxWidth(),
            placeholder = { Text("Search...") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    // Handle search action
                }
            ),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .weight(1f),
                columns = GridCells.Fixed(1),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                itemsIndexed(items = carList) { index, car ->
                    CarCard(car) {
                        navigateToCarPostDetails(car.id ?: "")
                    }
                }
            }
        }

        Button(
            modifier = Modifier
                .padding(16.dp)
                .padding(bottom = 72.dp)
                .align(Alignment.End),
            onClick =
            {
                if(userData?.phoneNumber.isNullOrEmpty()) {
                    isDialogVisible = true
                } else {
                    navigateToCreateCarPost()
                }
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                )
                Text("Add Post")
            }
        }

        if (isDialogVisible) {
            GoUpdateProfileDialog(
                onConfirm = {
                    isDialogVisible = false
                    navigateToProfile()
                },
                onCancel = { isDialogVisible = false }
            )
        }
    }

}
@Composable
fun GoUpdateProfileDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text("Update your phone number first")
        },
        confirmButton = {
            Button(
                onClick = onConfirm
            ) {
                Text("update")
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
fun CarCard(car: CarModel, navigateToCarPostDetails : (carId : String?) -> Unit ) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.background)
            .clickable { navigateToCarPostDetails(car.id) }
    ) {
        AsyncImage(
            model = car.images?.firstOrNull() ?: R.drawable.ic_launcher_background,
            contentDescription = stringResource(id = R.string.car_list_description),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${car.title}",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${car.price}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenReview() {
    val dummyUserData = UserEntity(
        userId = "001192394dua30",
        email = "John@gmail.com",
        username = "John",
        profilePicture = "",
        phoneNumber = "",
        premium = false,
    )

    val carList = listOf(
        CarModel("1", "Toyota", "Corolla", "$10,000"),
        CarModel("2", "Honda", "Civic", "$9,500"),
        CarModel("3", "Ford", "Mustang", "$20,000"),
        CarModel("4", "Honda", "Brio", "$4,000"),
        CarModel("5", "Audi", "I8", "$34,000"),
    )


    HomeScreen(dummyUserData, onSignOut = {}, carList, {}, {}, {})
}