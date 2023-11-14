package com.example.car_rental_project.composable.carpost

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.car_rental_project.state.CarPostState
import com.example.car_rental_project.view_model.CarViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.car_rental_project.model.CarModel

@Composable
fun CreateCarPostScreen(
    state : CarPostState,
    carViewModel: CarViewModel,
    storeToDatabase : () -> Unit,
) {
    Column {
        Row {
            PhotoPicker(
                storeToDatabase = {storeToDatabase()},
                carViewModel = carViewModel,
                state = state,
                )
        }
    }
}

@Composable
fun PhotoPicker(carViewModel: CarViewModel,
                storeToDatabase: () -> Unit,
                state : CarPostState
){

    val multiplePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = {
            carViewModel.onImagesChange(it)
        }
    )
    Column{
        LazyColumn{
            item{
                Button(onClick = {
                    multiplePhotoPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) {
                    Text("Pick Multiple Images")
                }
            }
            items(state.images ?: emptyList()){ image ->
                AsyncImage(model = image, contentDescription = null, modifier = Modifier.size(248.dp))
            }

        }
        Button(onClick = {
            storeToDatabase()
        }) {
            Text("Upload")
        }
    }

}
