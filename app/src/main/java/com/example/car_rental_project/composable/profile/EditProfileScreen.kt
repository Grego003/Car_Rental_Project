package com.example.car_rental_project.composable.profile

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.car_rental_project.model.UserEntity
import com.example.car_rental_project.model.UserModel
import com.example.car_rental_project.state.ProfileState
import com.example.car_rental_project.view_model.ProfileViewModel

@Composable
fun EditProfileScreen(
    userModel : UserEntity?,
    profileViewModel : ProfileViewModel,
    saveProfile : () -> Unit,
    ) {

    val state by profileViewModel.state.collectAsState()

    LaunchedEffect(true) {
        if(userModel != null) {
            profileViewModel.initializeOldData(userModel)
        }
    }

    Column {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(data = state.profilePicture ?: userModel?.profilePicture)
                        .apply(block = fun ImageRequest.Builder.() {
                            crossfade(true)
                        }).build()
                ),
                contentDescription = "Profile Picture",
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Crop,
            )

        TextField(
            value = state.username.orEmpty(),
            onValueChange = {
                    newUsername -> profileViewModel.onUsernameChange(newUsername)
            },
            label = { Text(text = "Username") }
        )
        TextField(
            value = state.phoneNumber.takeIf { it?.isNotBlank() == true } ?: "",
            onValueChange = {
                    newPhoneNumber -> profileViewModel.onPhoneNumberChange(newPhoneNumber)
            },
            label = { Text(text = "Phone Number") }
        )
        Row {
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(MaterialTheme.colorScheme.primary)
                .clip(RoundedCornerShape(16.dp)),
            onClick = saveProfile
        ) {
            Text(text = "Save Profile")
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
        Text(
            text = "user image",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Button(
            onClick = {
                multiplePhotoPicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(72.dp)
                .background(MaterialTheme.colorScheme.primary)
                .clip(RoundedCornerShape(16.dp)),
        ) {
            Text(text = "EDIT IMAGE HERE")
        }

        Row {
            AsyncImage(
                model = state.profilePicture,
                contentDescription = null,
                modifier = Modifier.size(248.dp)
            )
        }
    }
}