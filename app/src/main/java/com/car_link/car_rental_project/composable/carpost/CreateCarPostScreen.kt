package com.car_link.car_rental_project.composable.carpost

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import com.car_link.car_rental_project.state.CarPostState
import com.car_link.car_rental_project.view_model.CarViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.car_link.car_rental_project.model.CarCategory
import com.car_link.car_rental_project.model.CarCondition
import com.car_link.car_rental_project.model.CarModel
import com.car_link.car_rental_project.model.EngineCapasity
import com.car_link.car_rental_project.model.FuelType
import com.car_link.car_rental_project.view_model.NavViewModel
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.text.isDigitsOnly
import com.car_link.car_rental_project.model.UserEntity
import com.car_link.car_rental_project.model.UserModel
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter

@Composable
fun CreateCarPostScreen(
    state : CarPostState,
    carViewModel: CarViewModel,
    navViewModel: NavViewModel,
    navController: NavController,
    storeToDatabase : () -> Unit,
) {
    BackHandler {
        navViewModel.resetState()
        navController.popBackStack()
    }

    LazyColumn(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            CreateCarPostForm(state = state, carViewModel = carViewModel)
            Button(
                onClick =
                {
                    storeToDatabase()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Create")
            }
        }
    }
}

@Composable
fun CreateCarPostForm(
    modifier: Modifier = Modifier,
    state : CarPostState,
    carViewModel: CarViewModel,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(
            text = "Create Car Post",
            style = MaterialTheme.typography.titleLarge,
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
            PhotoPicker(carViewModel = carViewModel, state = state )
            Spacer(modifier = Modifier.height(8.dp))
            TitleTextInput(title = state.title, carViewModel =carViewModel)
            Spacer(modifier = Modifier.height(8.dp))
            BrandTextInput(brand = state.brand, carViewModel = carViewModel)
            Spacer(modifier = Modifier.height(8.dp))
            ModelTextInput(model = state.model, carViewModel = carViewModel)
            Spacer(modifier = Modifier.height(8.dp))
            ConditionDropDownMenu(condition = state.condition ?: CarCondition.BEKAS, carViewModel = carViewModel)
            Spacer(modifier = Modifier.height(8.dp))
            YearDropdownMenu(selectedYear = state.yearBought.toString(),carViewModel)
            Spacer(modifier = Modifier.height(8.dp))
            FuelTypeDropDownMenu(fuelType = state.fuelType?: FuelType.BENSIN, carViewModel = carViewModel)
            Spacer(modifier = Modifier.height(8.dp))
            OdometerNumberInput(odometer = state.odometer, carViewModel = carViewModel )
            Spacer(modifier = Modifier.height(8.dp))
            CategoryDropDownMenu(category = state.category?: CarCategory.CLASSIC_CAR, carViewModel =  carViewModel)
            Spacer(modifier = Modifier.height(8.dp))
            engineCapasityDropDownMenu(engineCapasity = state.engineCapasity?: EngineCapasity.CC_1000_TO_1500, carViewModel = carViewModel)
            Spacer(modifier = Modifier.height(8.dp))
            descriptionTextInput(description = state.description, carViewModel = carViewModel)
            Spacer(modifier = Modifier.height(8.dp))
            priceNumberInput(price = state.price, carViewModel = carViewModel)
        }
    }

@Composable
fun TitleTextInput(
    title: String?,
    modifier: Modifier = Modifier,
    carViewModel: CarViewModel
) {

    TextField(
        modifier = modifier
            .fillMaxWidth(),
        value = title.orEmpty(),
        onValueChange = { newTitle ->
            carViewModel.onTitleChange(newTitle)
        },
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
        modifier = modifier
            .fillMaxWidth(),
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
        modifier = modifier
            .fillMaxWidth()
        ,
        value = model.orEmpty(),
        onValueChange = { newModel -> carViewModel.onModelChange(newModel) },
        label = { Text(text = "Model") }
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
            modifier = modifier
                .fillMaxWidth()
                .menuAnchor(),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearDropdownMenu(selectedYear: String, carViewModel: CarViewModel, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        modifier = modifier
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            readOnly = true,
            value = selectedYear,
            onValueChange = {},
            label = { Text("Year") },
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
            (1900..Year.now().value).forEach { year ->
                DropdownMenuItem(
                    text = { Text(text = year.toString()) },
                    onClick = {
                        carViewModel.onYearBoughtChange(Year.parse(year.toString()))
                        expanded = false
                    }
                )
            }
        }
    }
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
            modifier = modifier
                .fillMaxWidth()
                .menuAnchor(),
            readOnly = true,
            value = fuelType.displayName,
            onValueChange = { },
            label = { Text("Fuel Type") },
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
        modifier = modifier
            .fillMaxWidth(),
        value = odometer.orEmpty(),
        onValueChange = { newOdometer: String ->
            if (newOdometer.isDigitsOnly()) {
                carViewModel.onOdometerChange(newOdometer)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = { Text(text = "Odometer") },
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
            modifier = modifier
                .fillMaxWidth()
                .menuAnchor(),
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
fun PhotoPicker(carViewModel: CarViewModel, state: CarPostState) {
    val multiplePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = {
            carViewModel.onImagesChange(it)
        }
    )
    Column {
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Default.AddCircle, contentDescription = null)
                Text("Pick Car Images", color = Color.White, style = MaterialTheme.typography.titleSmall)
            }
        }

        LazyRow {
            items(state.images ?: emptyList()) { image ->
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
            modifier = modifier
                .fillMaxWidth()
                .menuAnchor(),
            readOnly = true,
            value = engineCapasity.displayName,
            onValueChange = { },
            label = { Text("Engine Capacity") },
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
    carViewModel: CarViewModel
) {
    TextField(
        modifier = modifier
            .fillMaxWidth(),
        value = description.orEmpty(),
        onValueChange = { newDescription -> carViewModel.onDescriptionChange(newDescription) },
        label = { Text(text = "Description") },
        maxLines = 3,
        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
    )
}

@Composable
fun priceNumberInput(
    price: String?,
    modifier: Modifier = Modifier,
    carViewModel: CarViewModel)
{
    TextField(
        modifier = modifier
            .fillMaxWidth(),
        value = price.toString(),
        onValueChange = { newPrice ->
            if (newPrice.isDigitsOnly()) {
                carViewModel.onPriceChange(newPrice)
            }},
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = { Text(text = "Price (in Rupiah)") },
    )
}
