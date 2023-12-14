package com.example.car_rental_project.composable.profile

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.car_rental_project.model.UserEntity
import com.example.car_rental_project.model.UserModel
import com.example.car_rental_project.state.ProfileState
import com.example.car_rental_project.view_model.ProfileViewModel

@Composable
fun EditProfileScreen(
    userModel: UserEntity?,
    profileViewModel: ProfileViewModel,
    saveProfile: () -> Unit,
) {
    val state by profileViewModel.state.collectAsState()

    LaunchedEffect(true) {
        if (userModel != null) {
            profileViewModel.initializeOldData(userModel)
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.secondary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(data = state.profilePicture ?: userModel?.profilePicture)
                        .apply {
                            crossfade(true)
                        }
                        .build()
                ),
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))


        TextField(
            value = state.username.orEmpty(),
            modifier = Modifier
                .fillMaxWidth(),
            onValueChange = { newUsername ->
                profileViewModel.onUsernameChange(newUsername)
            },
            label = { Text(text = "Username") }
        )

        TextField(
            value = state.phoneNumber.takeIf { it?.isNotBlank() == true } ?: "",
            modifier = Modifier
                .fillMaxWidth(),
            onValueChange = { newPhoneNumber ->
                profileViewModel.onPhoneNumberChange(newPhoneNumber)
            },
            label = { Text(text = "Phone Number") }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Premium",
                modifier = Modifier.weight(1f)
            )
            Checkbox(
                checked = state.premium ?: false,
                onCheckedChange = { newCheckedState ->
                    profileViewModel.onPremiumChange(newCheckedState)
                }
            )
        }

        SinglePhotoPicker(profileViewModel, state)

        Button(
            onClick = saveProfile,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Save Profile", color = Color.White)
        }
    }
}


@Composable
fun SinglePhotoPicker(profileViewModel: ProfileViewModel, state: ProfileState) {
    val multiplePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            profileViewModel.onProfilePictureChange(it)
        }
    )
    Column {

        OutlinedButton(
            onClick = {
                multiplePhotoPicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp)),
        ) {
            Text(text = "EDIT IMAGE HERE", color = MaterialTheme.colorScheme.primary)
        }

        Row {
            AsyncImage(
                model = state.profilePicture,
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )
        }
    }
}