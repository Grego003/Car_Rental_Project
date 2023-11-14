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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.car_rental_project.R
import com.example.car_rental_project.model.CarModel
import com.example.car_rental_project.model.UserEntity

//Todo Rapihin ama buat design nya (dipecah pecah kaya login)
@Composable
fun HomeScreen(
    userData : UserEntity?,
    onSignOut : () -> Unit,
    carList: List<CarModel>,
    navigateToCreateCarPost : () -> Unit,
) {
    Column (
        modifier = Modifier
            .fillMaxSize()

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.LightGray)
                .padding(16.dp),
            horizontalArrangement = Arrangement
                .spacedBy(10.dp),
            verticalAlignment  = Alignment.CenterVertically,

        ) {
            Button(onClick = onSignOut) { Text(text = "Sign Out") }
            Spacer(modifier = Modifier.weight(1f))
            Text (
                text = userData?.username ?: "",
                style = MaterialTheme.typography.titleLarge
            )
            AsyncImage(
                model = userData?.profilePicture?.let { if (it.isEmpty()) null else it } ,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.profile_placeholder),
                fallback = painterResource(id = R.drawable.profile_placeholder),
            )
        }

        val (rightColumn, leftColumn) = carList.splitAt(carList.size / 2)

        Row(modifier = Modifier.fillMaxWidth()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(leftColumn) { car -> CarCard(car) }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(rightColumn) { car -> CarCard(car) }
            }
        }

        Button(
            modifier = Modifier.padding(8.dp),
            onClick = { navigateToCreateCarPost() }) {
            Text("Navigate to Create Car Post")
        }
    }
}

// GATAU INI APAAN
fun <T> List<T>.splitAt(index: Int): Pair<List<T>, List<T>> {
    return Pair(subList(0, index), subList(index, size))
}

@Composable
fun CarCard(car: CarModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.background)
            .clickable {}
    ) {
        AsyncImage(
            model = car.images?.firstOrNull() ?: R.drawable.ic_launcher_background,
            contentDescription = stringResource(id = R.string.car_list_description),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(125.dp)
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
        isVerified = false,
    )

    val carList = listOf(
        CarModel("1", "Toyota", "Corolla", "$10,000"),
        CarModel("2", "Honda", "Civic", "$9,500"),
        CarModel("3", "Ford", "Mustang", "$20,000"),
        CarModel("4", "Honda", "Brio", "$4,000"),
        CarModel("5", "Audi", "I8", "$34,000"),
    )


    HomeScreen(dummyUserData, onSignOut = {}, carList, {})
}