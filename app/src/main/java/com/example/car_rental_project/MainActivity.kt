package com.example.car_rental_project

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.car_rental_project.composable.Nav.BottomNavigation
import com.example.car_rental_project.composable.auth.LoginScreen
import com.example.car_rental_project.composable.auth.RegisterScreen
import com.example.car_rental_project.composable.carpost.CarPostDetailScreen
import com.example.car_rental_project.composable.carpost.CreateCarPostScreen
import com.example.car_rental_project.composable.carpost.UserCarPostScreen
import com.example.car_rental_project.composable.extras.LoadingScreen
import com.example.car_rental_project.composable.home.HomeScreen
import com.example.car_rental_project.composable.invoice.InvoiceScreen
import com.example.car_rental_project.composable.profile.ProfileScreen
import com.example.car_rental_project.model.CarCategory
import com.example.car_rental_project.model.CarCondition
import com.example.car_rental_project.model.CarModel
import com.example.car_rental_project.model.EngineCapasity
import com.example.car_rental_project.model.FuelType
import com.example.car_rental_project.model.UserEntity
import com.example.car_rental_project.repository.CarPostRepository
import com.example.car_rental_project.service.FirebaseDBService
import com.example.car_rental_project.service.FirebaseStorageService
import com.example.car_rental_project.service.GoogleAuthService
import com.example.car_rental_project.ui.theme.Car_Rental_ProjectTheme
import com.example.car_rental_project.view_model.CarViewModel
import com.example.car_rental_project.view_model.NavViewModel
import com.example.car_rental_project.view_model.SignInViewModel
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch
import java.time.Year

class MainActivity : ComponentActivity() {
    private val googleAuthService by lazy {
        GoogleAuthService(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext),
            firebaseService = FirebaseDBService
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val carRepository = CarPostRepository(FirebaseDBService, FirebaseStorageService)
        setContent {
            Car_Rental_ProjectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                )
                {
                    val authViewModel = viewModel<SignInViewModel>()
                    val carViewModel = viewModel<CarViewModel>()
                    val navViewModel = viewModel<NavViewModel>()

                    var userData by remember { mutableStateOf<UserEntity?>(null) }
                    var carsData by remember { mutableStateOf<List<CarModel>?>(null) }
                    var userPostData by remember { mutableStateOf<List<CarModel>?>(null)}
                    var carData by remember { mutableStateOf<CarModel?>(null) }

                    val authState by authViewModel.state.collectAsStateWithLifecycle()
                    val navState by navViewModel.state.collectAsStateWithLifecycle()

                    val navController = rememberNavController()

                    NavHost(navController, startDestination = "login") {

                        composable("login") {

                            LaunchedEffect(key1 = Unit) {
                                if (googleAuthService.getSignedInUser() != null) {
                                    navController.navigate("home")
                                }
                            }

                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    if (result.resultCode == RESULT_OK) {
                                        authViewModel.onLoad()
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthService.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            authViewModel.onSignInResult(signInResult)
                                        }
                                    } else {
                                        Log.d("onResult", "ActivityResult: " + result.toString())
                                    }
                                }
                            )
                            // login with email and password
                            LaunchedEffect(authState.isSignInSuccessful) {
                                if (authState.isSignInSuccessful) {
                                    navController.navigate("home")
                                    Toast.makeText(
                                        applicationContext,
                                        R.string.Sign_in_Successful,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                authViewModel.onLoadFinished()
                            }

                            LoginScreen(
                                viewModel = authViewModel,
                                state = authState,
                                onSignInClickWithGoogleTapiIn = {
                                    lifecycleScope.launch {
                                        val signInIntentSender =
                                            googleAuthService.signInWithGoogleOneTapClient()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntentSender ?: return@launch
                                            ).build()
                                        )
                                    }
                                },
                                onSignInClickWithEmail = { email, password ->
                                    authViewModel.onLoad()
                                    lifecycleScope.launch {
                                        val signInResult =
                                            googleAuthService.signInUserWithEmailAndPassword(
                                                email,
                                                password
                                            )
                                        authViewModel.onSignInResult(signInResult)
                                    }
                                },
                                onToCreateAccountScreen = {
                                    navController.navigate("register")
                                    authViewModel.resetState()
                                }
                            )
                        }
                        composable("register") {

                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    if (result.resultCode == RESULT_OK) {
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthService.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            authViewModel.onSignInResult(signInResult)
                                        }
                                    } else {
                                        Log.d("onResult", "ActivityResult: " + result.toString())
                                    }
                                }
                            )
                            LaunchedEffect(key1 = authState.isSignInSuccessful) {
                                if (authState.isSignInSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        R.string.Sign_in_Successful,
                                        Toast.LENGTH_LONG
                                    ).show()

                                    navController.navigate("home")
                                    // authViewModel.resetState()
                                }
                                authViewModel.onLoadFinished()
                            }

                            RegisterScreen(
                                viewModel = authViewModel,
                                state = authState,
                                onSignUpClickWithGoogleTapiIn = {
                                    lifecycleScope.launch {
                                        val signInIntentSender =
                                            googleAuthService.signInWithGoogleOneTapClient()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntentSender ?: return@launch
                                            ).build()
                                        )
                                    }
                                },
                                onSignUpClickWithEmail = { email, password, username ->
                                    lifecycleScope.launch {
                                        val signInResult =
                                            googleAuthService.createUserWithEmailAndPassword(
                                                username = username,
                                                email = email,
                                                password = password
                                            )
                                        authViewModel.onSignInResult(signInResult)
                                    }
                                },
                                onToLoginScreen = {
                                    navController.navigate("login")
                                    authViewModel.resetState()
                                }

                            )
                        }
                        composable("home") {
                            LaunchedEffect(carData) {
                                if(carData == null) {
                                    userData = googleAuthService.getSignedInUser()
                                    carRepository.getAllCarPosts().collect { carList ->
                                        carsData = carList
                                    }
                                    Log.d("CARSDATA", carsData.toString())
                                }
                            }
                            BottomNavigation(navController = navController, navViewModel = navViewModel)
                            HomeScreen(
                                userData = userData,
                                onSignOut = {
                                    lifecycleScope.launch {
                                        googleAuthService.signOut()
                                        Toast.makeText(
                                            applicationContext,
                                            R.string.Signed_Out,
                                            Toast.LENGTH_LONG
                                        ).show()
                                        authViewModel.resetState()
                                        navController.navigate("login")
                                    }
                                },
                                carList = carsData ?: emptyList(),
                                navigateToCreateCarPost = {
                                    navController.navigate("createCarPost")
                                },
                                navigateToCarPostDetails = { carId : String ->
                                    lifecycleScope.launch {
                                        try {
                                            val result = carRepository.getCarPostById(carId)
                                            if (result.data == null) {
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Car Id Not Found",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                navController.navigate("home")
                                            } else {
                                                carData = result.data
                                            }
                                        } catch (e: Exception) {
                                            Log.e("CarDetail", e.toString())
                                        }
                                        if(carData != null) {
                                            navController.navigate("carPostDetails/${carId}")
                                        }
                                    }
                                }
                            )

                        }
                        composable("createCarPost") {

                            val carPostState by carViewModel.state.collectAsStateWithLifecycle()

                            LaunchedEffect(key1 = carPostState.isCreatePostSuccessful) {
                                if (carPostState.isCreatePostSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Car Successfully created",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    navController.navigate("home")
                                    carViewModel.resetState()
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        carPostState.errorMessage,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                            CreateCarPostScreen(
                                state = carPostState,
                                carViewModel = carViewModel,
                                navViewModel = navViewModel,
                                navController = navController,
                                storeToDatabase = {
                                    lifecycleScope.launch {
                                        val carModel = CarModel(
                                            title = carPostState.title,
                                            brand = carPostState.brand,
                                            model = carPostState.model,
                                            condition = carPostState.condition,
                                            yearBought = Year.of(2023).toString(),
                                            fuelType = carPostState.fuelType,
                                            odometer = carPostState.odometer?.toInt() ?: 0,
                                            category = carPostState.category,
                                            engineCapasity = carPostState.engineCapasity,
                                            description = carPostState.description,
                                            price = carPostState.price?.toInt() ?: 0,
                                            legalRequirements = true
                                        )
                                        val createCarPostResult = carRepository.createCarPost(
                                            userId = userData?.userId,
                                            carModel = carModel,
                                            images = carPostState.images ?: emptyList(),
                                            contextResolver = contentResolver
                                        )
                                        carViewModel.onCreateResult(createCarPostResult)
                                    }
                                }
                            )
                        }

                        composable("carPostDetails/{id}",
                            arguments = listOf(
                                navArgument("id") {
                                    type = NavType.StringType
                                }
                            )
                        ){
                            carData?.let {
                                CarPostDetailScreen(navController=navController, carData = it) }
                        }

                        composable("profile") {
                            BottomNavigation(navController = navController, navViewModel = navViewModel)
                            ProfileScreen(
                                user = userData,
                                onSignOut = {
                                    lifecycleScope.launch {
                                        googleAuthService.signOut()
                                        Toast.makeText(
                                            applicationContext,
                                            R.string.Signed_Out,
                                            Toast.LENGTH_LONG
                                        ).show()
                                        authViewModel.resetState()
                                        navController.navigate("login")
                                    }
                                },
                                navigateToUserCarPost = {
                                    navController.navigate("userCarPosts")
                                }

                            )
                        }

                        composable("invoice")
                        {
                            BottomNavigation(navController = navController, navViewModel = navViewModel)
                            InvoiceScreen()
                        }

                        composable("userCarPosts") {
                            LaunchedEffect(userPostData) {
                                if(userData != null) {
                                    userPostData = carRepository.getUserCarPosts(userData)
                                }
                                else {
                                    userData = googleAuthService.getSignedInUser()
                                }
                            }
                            UserCarPostScreen(
                                carList = userPostData,
                                navigateToCarPostDetails = { carId : String ->
                                    lifecycleScope.launch {
                                        try {
                                            val result = carRepository.getCarPostById(carId)
                                            if (result.data == null) {
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Car Id Not Found",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                navController.navigate("home")
                                            } else {
                                                carData = result.data
                                            }
                                        } catch (e: Exception) {
                                            Log.e("CarDetail", e.toString())
                                        }
                                        if(carData != null) {
                                            navController.navigate("carPostDetails/${carId}")
                                        }
                                    }
                                }
                            )
                        }


                    }
                }
            }
        }
    }
}

