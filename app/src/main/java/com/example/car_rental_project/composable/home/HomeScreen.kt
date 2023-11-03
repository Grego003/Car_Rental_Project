package com.example.car_rental_project.composable.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.car_rental_project.R
import com.example.car_rental_project.model.UserModel

//Todo Rapihin ama buat design nya (dipecah pecah kaya login)
@Composable
fun HomeScreen(
    userData : UserModel?,
    onSignOut : () -> Unit,
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
            //todo bikin button jadi kaya spinner kemaren
            Button(onClick = onSignOut) { Text(text = "Sign Out") }
            Spacer(modifier = Modifier.weight(1f))
            Text (
                text = userData?.username ?: "",
                style = MaterialTheme.typography.titleLarge
            )
            AsyncImage(
                model = userData?.profilePicture,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.profile_placeholder),
                fallback = painterResource(id = R.drawable.profile_placeholder),
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun HomeScreenReview() {
    val dummyUserData = UserModel(
        userId = "001192394dua30",
        username = "John",
        profilePicture = null
    )

    HomeScreen(dummyUserData, onSignOut = {})
}

