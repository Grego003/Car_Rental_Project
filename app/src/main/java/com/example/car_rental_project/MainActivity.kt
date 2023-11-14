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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.car_rental_project.composable.auth.LoginScreen
import com.example.car_rental_project.composable.auth.RegisterScreen
import com.example.car_rental_project.composable.carpost.CarPostDetailScreen
import com.example.car_rental_project.composable.carpost.CreateCarPostScreen
import com.example.car_rental_project.composable.extras.LoadingScreen
import com.example.car_rental_project.composable.home.HomeScreen
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

                    var userData by remember { mutableStateOf<UserEntity?>(null) }
                    var carsData by remember { mutableStateOf<List<CarModel>?>(null) }
                    var carData by remember { mutableStateOf<CarModel?>(null) }

                    val authState by authViewModel.state.collectAsStateWithLifecycle()
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
                            DisposableEffect(authState.isSignInSuccessful) {
                                if (authState.isSignInSuccessful) {
                                    navController.navigate("home")
                                    Toast.makeText(
                                        applicationContext,
                                        R.string.Sign_in_Successful,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                onDispose {
                                }
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
                            LaunchedEffect(key1 = Unit) {
                                if (googleAuthService.getSignedInUser() != null) {
                                    navController.navigate("home")
                                }
                            }
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
                                authViewModel.onLoadFinished()
                                if (authState.isSignInSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        R.string.Sign_in_Successful,
                                        Toast.LENGTH_LONG
                                    ).show()

                                    navController.navigate("home")
                                    authViewModel.resetState()
                                }
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

                            LaunchedEffect(Unit) {
                                userData = googleAuthService.getSignedInUser()
                                Log.d("USERDATA", googleAuthService.getSignedInUser().toString())
                                carRepository.getAllCarPosts().collect { carList ->
                                    carsData = carList
                                    // Perform any additional operations with the data if needed
                                }
                            }

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
                                    navController.navigate("carPostDetails/${carId}")
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
                                storeToDatabase = {
                                    // TODO masih hardcoded untuk masukin setiap value data :)
                                    lifecycleScope.launch {
                                        val carModel = CarModel(
                                            title = "Toyota Corolla 2",
                                            brand = "Toyota",
                                            model = "Corolla",
                                            condition = CarCondition.BEKAS,
                                            yearBought = Year.of(2019).toString(),
                                            fuelType = FuelType.BENSIN,
                                            odometer = 30000,
                                            category = CarCategory.SEDAN,
                                            engineCapasity = EngineCapasity.CC_1500_TO_2000,
                                            description = "A reliable sedan with low mileage.",
                                            price = 12000,
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
                            LaunchedEffect(Unit) {
                                try {
                                    val result = carRepository.getCarPostById(it.arguments?.getString("id")?: "")
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
                            }

                            carData?.let { CarPostDetailScreen(carData = it) }
                        }
                    }
                }
            }
        }
    }
}

