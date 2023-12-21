package com.car_link.car_rental_project.composable.home

import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.car_link.car_rental_project.R
import com.car_link.car_rental_project.model.CarModel
import com.car_link.car_rental_project.model.UserEntity
import java.text.NumberFormat
import java.util.Locale

//Todo Rapihin ama buat design nya (dipecah pecah kaya login)

@Composable
fun TabBar(
    selectedTabIndex: Int,
    onChangeTab: (index: Int) -> Unit,
) {
    Column {
        SecondaryTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = Color.Black
                )
            }
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                selectedContentColor = Color.White,
                onClick = { onChangeTab(0) },
                modifier = Modifier
                    .background(
                        color = if (selectedTabIndex == 0) MaterialTheme.colorScheme.primary else Color.LightGray,
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Normal")
            }

            Tab(
                selected = selectedTabIndex == 1,
                selectedContentColor = Color.White,
                onClick = { onChangeTab(1) },
                modifier = Modifier
                    .background(
                        color = if (selectedTabIndex == 1) Color.Magenta else Color.LightGray,
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Premium")
            }
        }
    }
}


@Composable
fun HomeScreen(
    userData: UserEntity?,
    onSignOut: () -> Unit,
    carList: List<CarModel>,
    navigateToCreateCarPost: () -> Unit,
    navigateToCarPostDetails: (carId: String) -> Unit,
    navigateToProfile : () ->Unit,
) {
    var isDialogVisible by remember { mutableStateOf(false) }
    var filteredCarList by remember { mutableStateOf(carList) }
    var searchFilter by remember { mutableStateOf("")}
    var selectedTabIndex by remember { mutableStateOf(0)}

    LaunchedEffect(carList){
        filteredCarList = carList.filter {
            it.premiumPost != true
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(color = Color.LightGray)
//                .padding(16.dp),
//            horizontalArrangement = Arrangement
//                .spacedBy(10.dp),
//            verticalAlignment = Alignment.CenterVertically,
//        ) {
//            Spacer(modifier = Modifier.weight(1f))
//            Text(
//                text = userData?.username ?: "",
//                style = MaterialTheme.typography.titleLarge
//            )
//            AsyncImage(
//                model = userData?.profilePicture?.let { if (it.isEmpty()) null else it },
//                contentDescription = "Profile Picture",
//                modifier = Modifier
//                    .size(40.dp)
//                    .clip(CircleShape),
//                contentScale = ContentScale.Crop,
//                placeholder = painterResource(id = R.drawable.profile_placeholder),
//                fallback = painterResource(id = R.drawable.profile_placeholder),
//            )
//        }
        TextField(
            value = searchFilter,
            onValueChange = { newFilter ->
                searchFilter = newFilter
                filteredCarList = carList.filter { car ->
                    val matchesTab = if (selectedTabIndex == 0) {
                        car.premiumPost != true
                    } else {
                        car.premiumPost == true
                    }
                    val matchesSearch = car.title?.contains(searchFilter, ignoreCase = true) ?: false
                    matchesTab && matchesSearch
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp),
            placeholder = {
                Text("Search...", color = Color.Gray)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                }
            ),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.secondary),
        )
        TabBar(
            selectedTabIndex = selectedTabIndex,
            onChangeTab = { newTabIndex ->
                selectedTabIndex = newTabIndex
                filteredCarList = carList.filter {
                    if (newTabIndex == 0) {
                        it.premiumPost != true
                    } else {
                        it.premiumPost == true
                    }
                }
            }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
//                .padding(bottom = 50.dp)
        ) {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .zIndex(1.0f),
                columns = GridCells.Fixed(1),
            ) {
                itemsIndexed(items = filteredCarList) { index, car ->
                    CarCard(car) {
                        navigateToCarPostDetails(car.id ?: "")
                    }
                }
            }
        }

        Button(
            onClick = {
                if (userData?.phoneNumber.isNullOrEmpty()) {
                    isDialogVisible = true
                } else {
                    navigateToCreateCarPost()
                }
            },
            modifier = Modifier
                .padding(bottom = 80.dp)
                .padding(end = 8.dp)
                .align(Alignment.End)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                Text("Add Post", style = MaterialTheme.typography.titleMedium)
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
                Text("Update")
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
fun CarCard(car: CarModel, navigateToCarPostDetails: (carId: String?) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(MaterialTheme.shapes.large)
            .padding(16.dp)
//            .border(2.dp, Color.Gray, shape = MaterialTheme.shapes.small)
            .shadow(8.dp, MaterialTheme.shapes.small)
            .clickable { navigateToCarPostDetails(car.id) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            val painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(data = car.images?.firstOrNull() ?: R.drawable.ic_launcher_background)
                    .apply(block = fun ImageRequest.Builder.() {
                        fadeIn(animationSpec = TweenSpec(1000))
                    }).build()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(MaterialTheme.shapes.large),
            ) {
                Image(
                    painter = painter,
                    contentDescription = stringResource(id = R.string.car_list_description),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )

                if (painter.state is AsyncImagePainter.State.Error) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_hide_image_24),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Rp ${formatPrice(car.price)}",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 8.dp),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = car.title ?: "",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 8.dp),
            )
        }
    }
}


fun formatPrice(price: Long?): String {
    if(price != null) {
        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
        return numberFormat.format(price)
    } else
        return "0"
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