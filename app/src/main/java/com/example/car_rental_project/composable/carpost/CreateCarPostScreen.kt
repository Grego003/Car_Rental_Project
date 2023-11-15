package com.example.car_rental_project.composable.carpost

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import com.example.car_rental_project.state.CarPostState
import com.example.car_rental_project.view_model.CarViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.car_rental_project.model.CarCategory
import com.example.car_rental_project.model.CarCondition
import com.example.car_rental_project.model.CarModel
import com.example.car_rental_project.model.EngineCapasity
import com.example.car_rental_project.model.FuelType

@Composable
fun CreateCarPostScreen(
    state : CarPostState,
    carViewModel: CarViewModel,
    storeToDatabase : () -> Unit,
) {
    Column {
        createCarPostForm(state = state, carViewModel = carViewModel)
        Button(onClick = {
            storeToDatabase()
        }) {
            Text("Create")
        }
    }
}

@Composable
fun createCarPostForm(
    modifier: Modifier = Modifier,
    state : CarPostState,
    carViewModel: CarViewModel,
) {
    Column {
            TitleTextInput(title = state.title, carViewModel =carViewModel)
            BrandTextInput(brand = state.brand, carViewModel = carViewModel)
            ModelTextInput(model = state.model, carViewModel = carViewModel)
            ConditionDropDownMenu(condition = state.condition ?: CarCondition.BEKAS, carViewModel = carViewModel)
            FuelTypeDropDownMenu(fuelType = state.fuelType?: FuelType.BENSIN, carViewModel = carViewModel)
            OdometerNumberInput(odometer = state.odometer, carViewModel = carViewModel )
            CategoryDropDownMenu(category = state.category?: CarCategory.CLASSIC_CAR, carViewModel =  carViewModel)
            PhotoPicker(carViewModel = carViewModel, state = state )
            engineCapasityDropDownMenu(engineCapasity = state.engineCapasity?: EngineCapasity.CC_1000_TO_1500, carViewModel = carViewModel)
            descriptionTextInput(description = state.description, carViewModel = carViewModel)
            priceNumberInput(price = state.price, carViewModel = carViewModel)
    }
}
@Composable
fun TitleTextInput(
    title: String?,
    modifier: Modifier = Modifier,
    carViewModel: CarViewModel)
{
    TextField(
        modifier = modifier,
        value = title.orEmpty(),
        onValueChange = { newTitle -> carViewModel.onTitleChange(newTitle) },
        label = { Text(text = "Title") }
    )
}

@Composable
fun BrandTextInput(
    brand: String?,
    modifier: Modifier = Modifier,
    carViewModel: CarViewModel)
{
    TextField(
        modifier = modifier,
        value = brand.orEmpty(),
        onValueChange = { newBrand -> carViewModel.onBrandChange(newBrand) },
        label = { Text(text = "Brand") }
    )
}

@Composable
fun ModelTextInput(
    model: String?,
    modifier: Modifier = Modifier,
    carViewModel: CarViewModel)
{
    TextField(
        modifier = modifier,
        value = model.orEmpty(),
        onValueChange = { newModel -> carViewModel.onModelChange(newModel) },
        label = { Text(text = "model") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConditionDropDownMenu(
    condition: CarCondition,
    modifier: Modifier = Modifier,
    carViewModel: CarViewModel)
{
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = condition.displayName,
            onValueChange = {  },
            label = { Text("Condition") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            modifier = Modifier.menuAnchor(),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
           CarCondition.values().forEach { condition ->
               DropdownMenuItem(
                   text = { Text(text = condition.displayName) },
                   onClick = { carViewModel.onConditionChange(condition) }
               )
           }
        }
    }
}
@Composable
fun YearCreatedPicker() {
    /*TODO tolong buatin ini bisa milih tanggal :)*/
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelTypeDropDownMenu(
    fuelType: FuelType,
    modifier: Modifier = Modifier,
    carViewModel: CarViewModel)
{
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = fuelType.displayName,
            onValueChange = { },
            label = { Text("FuelType") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            FuelType.values().forEach { fuel ->
                DropdownMenuItem(
                    text = { Text(text = fuel.displayName) },
                    onClick = { carViewModel.onFuelTypeChange(fuel) })
            }
        }
    }
}
@Composable
fun OdometerNumberInput(
    odometer: String?,
    modifier: Modifier = Modifier,
    carViewModel: CarViewModel)
{
    TextField(
        modifier = modifier,
        value = odometer.orEmpty(),
        onValueChange = { newOdometer: String? ->
            carViewModel.onOdometerChange(newOdometer)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = { Text(text = "odometer") },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropDownMenu(
    category: CarCategory,
    modifier: Modifier = Modifier,
    carViewModel: CarViewModel)
{
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = category.displayName,
            onValueChange = { },
            label = { Text("Category") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            CarCategory.values().forEach { category ->
                DropdownMenuItem(
                    text = { Text(text = category.displayName) },
                    onClick = { carViewModel.onCategoryChange(category) })
            }
        }
    }
}

@Composable
fun PhotoPicker(carViewModel: CarViewModel,
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
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun engineCapasityDropDownMenu(
    engineCapasity: EngineCapasity,
    modifier: Modifier = Modifier,
    carViewModel: CarViewModel)
{
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = engineCapasity.displayName,
            onValueChange = { },
            label = { Text("engineCapasity") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            EngineCapasity.values().forEach { engine ->
                DropdownMenuItem(
                    text = { Text(text = engine.displayName) },
                    onClick = { carViewModel.onEngineCapasityChange(engine) })
            }
        }
    }
}
@Composable
fun descriptionTextInput(
    description: String?,
    modifier: Modifier = Modifier,
    carViewModel: CarViewModel)
{
    TextField(
        modifier = modifier,
        value = description.orEmpty(),
        onValueChange = { newDescription -> carViewModel.onDescriptionChange(newDescription) },
        label = { Text(text = "Description") }
    )
}

@Composable
fun priceNumberInput(
    price: String?,
    modifier: Modifier = Modifier,
    carViewModel: CarViewModel)
{
    TextField(
        modifier = modifier,
        value = price.toString(),
        onValueChange = { newPrice -> carViewModel.onPriceChange(newPrice) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = { Text(text = "price") },
    )
}


