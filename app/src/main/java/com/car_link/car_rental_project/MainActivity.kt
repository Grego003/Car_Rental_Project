package com.car_link.car_rental_project

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.car_link.car_rental_project.composable.Nav.BottomNavigation
import com.car_link.car_rental_project.composable.auth.LoginScreen
import com.car_link.car_rental_project.composable.auth.RegisterScreen
import com.car_link.car_rental_project.composable.carpost.CarPostDetailScreen
import com.car_link.car_rental_project.composable.carpost.CreateCarPostScreen
import com.car_link.car_rental_project.composable.carpost.UserCarPostScreen
import com.car_link.car_rental_project.composable.extras.LoadingScreen
import com.car_link.car_rental_project.composable.home.HomeScreen
import com.car_link.car_rental_project.composable.invoice.InvoiceScreen
import com.car_link.car_rental_project.composable.profile.EditProfileScreen
import com.car_link.car_rental_project.composable.profile.ProfileScreen
import com.car_link.car_rental_project.composable.transaction.TransactionScreen
import com.car_link.car_rental_project.model.CarCategory
import com.car_link.car_rental_project.model.CarCondition
import com.car_link.car_rental_project.model.CarModel
import com.car_link.car_rental_project.model.EngineCapasity
import com.car_link.car_rental_project.model.FuelType
import com.car_link.car_rental_project.model.TransactionEntity
import com.car_link.car_rental_project.model.TransactionStatus
import com.car_link.car_rental_project.model.UserEntity
import com.car_link.car_rental_project.repository.CarPostRepository
import com.car_link.car_rental_project.repository.TransactionRepository
import com.car_link.car_rental_project.service.FirebaseDBService
import com.car_link.car_rental_project.service.FirebaseStorageService
import com.car_link.car_rental_project.service.GoogleAuthService
import com.car_link.car_rental_project.ui.theme.Car_Rental_ProjectTheme
import com.car_link.car_rental_project.view_model.CarViewModel
import com.car_link.car_rental_project.view_model.NavViewModel
import com.car_link.car_rental_project.view_model.ProfileViewModel
import com.car_link.car_rental_project.view_model.SignInViewModel
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.time.Year

class MainActivity : ComponentActivity() {
    private val googleAuthService by lazy {
        GoogleAuthService(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext),
            firebaseService = FirebaseDBService,
            firebaseStorage = FirebaseStorageService,
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val carRepository = CarPostRepository(FirebaseDBService, FirebaseStorageService)
        val transactionRepository = TransactionRepository(FirebaseDBService)
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
                    val profileViewModel = viewModel<ProfileViewModel>()

                    var userData by remember { mutableStateOf<UserEntity?>(null) }
                    var carsData by remember { mutableStateOf<List<CarModel>?>(null) }
                    var userPostData by remember { mutableStateOf<List<CarModel>?>(null) }
                    var carData by remember { mutableStateOf<CarModel?>(null) }
                    var transactionData by remember { mutableStateOf<List<TransactionEntity>?>(null) }

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
                                authViewModel.onLoadFinished()
                                userData = googleAuthService.getSignedInUser()
                                if (authState.isSignInSuccessful) {
                                    navController.navigate("home")
                                    Toast.makeText(
                                        applicationContext,
                                        R.string.Sign_in_Successful,
                                        Toast.LENGTH_LONG
                                    ).show()
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
                                        if(signInResult.data != null) {
                                            authViewModel.onSignInResult(signInResult)
                                        }
                                        else {
                                            Toast.makeText(
                                                applicationContext,
                                                "Email or Password are wrong",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            authViewModel.resetState()
                                        }
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
                                        Log.d("onResult", "intentResult: " + result.toString())
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
                                     authViewModel.resetState()
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
                                        userData = signInResult.data
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
                                carRepository.getAllCarPosts().collect { carList ->
                                    carsData = carList
                                }
                            }
                            BottomNavigation(
                                navController = navController,
                                navViewModel = navViewModel,
                            )
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
                                carList = carsData?.reversed() ?: emptyList(),
                                navigateToCreateCarPost = {
                                    navController.navigate("createCarPost")
                                },
                                navigateToProfile = {
                                    navController.navigate("profile")
                                },
                                navigateToCarPostDetails = { carId: String ->
                                    lifecycleScope.launch {
                                        try {
                                            val result = carRepository.getCarPostById(carId)
                                            if (result.data == null) {
                                                Toast.makeText(
                                                    applicationContext,
                                                    result.errorMessage,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                navController.navigate("home")
                                            } else {
                                                carData = result.data
                                            }
                                        } catch (e: Exception) {
                                            Log.e("CarDetail", e.toString())
                                        }
                                        if (carData != null) {
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
                                            sellerName = userData?.username,
                                            condition = carPostState.condition,
                                            phoneNumber = userData?.phoneNumber,
                                            yearBought = carPostState.yearBought.toString(),
                                            fuelType = carPostState.fuelType,
                                            odometer = carPostState.odometer?.toInt() ?: 0,
                                            category = carPostState.category,
                                            engineCapasity = carPostState.engineCapasity,
                                            description = carPostState.description,
                                            price = carPostState.price?.toLong() ?: 0,
                                            premiumPost = userData?.premium,
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
                        ) {
                            carData?.let {
                                CarPostDetailScreen(
                                    authUser = userData,
                                    navController = navController,
                                    carData = it,
                                    context = applicationContext,
                                    navigateToProfile = {
                                        navController.navigate("profile")
                                    },
                                    createTransaction = {
                                        lifecycleScope.launch {
                                            val result = transactionRepository.createTransaction(
                                                TransactionEntity(
                                                    postCarId = it.id,
                                                    buyerId = userData?.userId,
                                                    buyerName = userData?.username,
                                                    sellerName = it.sellerName,
                                                    sellerId = it.userId,
                                                    carPost = it
                                                )
                                            )
                                            if (result.data != null) {
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Transaction successfuly created",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                navController.navigate("transaction")
                                            } else {
                                                Toast.makeText(
                                                    applicationContext,
                                                    result.errorMessage.toString(),
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    })
                            }
                        }

                        composable("profile") {
                            LaunchedEffect(key1 = userData) {
                                userData = googleAuthService.getSignedInUser()
                            }
                            BottomNavigation(
                                navController = navController,
                                navViewModel = navViewModel,
                            )
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
                                },
                                navigateToEditProfile = {
                                    navController.navigate("editUserProfile")
                                }

                            )
                        }

                        composable("invoice")
                        {
                            LaunchedEffect(key1 = transactionData) {
                                transactionRepository.getAllTransaction().collect { transaction ->
                                    transactionData = transaction
                                }
                            }
                            BottomNavigation(
                                navController = navController,
                                navViewModel = navViewModel,
                            )
                            InvoiceScreen(transactionData, userData)

                        }

                        composable("editUserProfile") {

                            val state by profileViewModel.state.collectAsState()

                            LaunchedEffect(userData, state.isUpdateSuccessful) {
                                if (userData == null || state.isUpdateSuccessful) {
                                    userData = googleAuthService.getSignedInUser()
                                    profileViewModel.resetState()
                                }
                            }
                            EditProfileScreen(userModel = userData,
                                profileViewModel = profileViewModel,
                                saveProfile = {
                                    val updatedUserData = UserEntity(
                                        email = state.email,
                                        userId = state.userId,
                                        username = state.username,
                                        phoneNumber = state.phoneNumber,
                                        premium = state.premium,
                                    )
                                    lifecycleScope.launch {
                                        val updateResult = googleAuthService.editUserProfile(
                                            updatedUserData,
                                            state.profilePicture,
                                            contentResolver
                                        )

                                        if (updateResult.data == null) {
                                            Toast.makeText(
                                                applicationContext,
                                                updateResult.errorMessage,
                                                Toast.LENGTH_LONG
                                            ).show()
                                            navController.popBackStack()
                                        } else {
                                            Toast.makeText(
                                                applicationContext,
                                                "update data is Successful",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            profileViewModel.onUpdateResult(updateResult)
                                            navController.navigate("profile")
                                        }
                                    }
                                })
                        }

                        composable("userCarPosts") {

                            val userCarPostsState = remember { MutableStateFlow<List<CarModel>>(emptyList()) }

                            LaunchedEffect(userData) {
                                if (userData != null) {
                                    carRepository.getUserCarPosts(userData)?.collect { carList ->
                                        userCarPostsState.value = carList
                                    }
                                }
                            }

                            UserCarPostScreen(
                                carList = userCarPostsState.collectAsState().value,
                                navigateToCarPostDetails = { carId: String ->
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
                                        if (carData != null) {
                                            navController.navigate("carPostDetails/${carId}")
                                        }
                                    }
                                },
                                deletePost = { id ->
                                    lifecycleScope.launch {
                                        val isDeletionSuccess = carRepository.deleteCarPost(id)
                                        if(isDeletionSuccess) {
                                            Toast.makeText(
                                                applicationContext,
                                                "Car Post successfuly deleted",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                        else {
                                            Toast.makeText(
                                                applicationContext,
                                                "Car post failed to be deleted",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }
                            )
                        }
                        composable("transaction")

                        {
                            LaunchedEffect(key1 = transactionData) {
                                transactionRepository.getAllTransaction().collect { transaction ->
                                    transactionData = transaction
                                }
                            }
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                BottomNavigation(
                                    navController = navController,
                                    navViewModel = navViewModel,
                                )
                                TransactionScreen(
                                    authUser = userData,
                                    transactionData = transactionData,
                                    rejectTransaction = { transaction ->
                                        lifecycleScope.launch {
                                            val result = transactionRepository.deleteTransaction(transaction.id ?: "")
                                            if(result) {
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Transaction Success",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                transactionRepository.getAllTransaction().collect { transaction ->
                                                    transactionData = transaction
                                                }
                                            }
                                            else {
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Some Thing When Wrong.. .",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    },
                                    acceptTransaction = { transaction ->
                                        lifecycleScope.launch {
                                            val result = transactionRepository.updateTransaction(TransactionStatus.FINISHED, transaction)
                                            if(result.data != null) {
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Transaction Success",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                carRepository.deleteCarPost(transaction.carPost?.id ?: "")
//                                                    carRepository.getAllCarPosts().collect { car ->
//                                                        carsData = car
//                                                    }
                                                val updateUserMoney = userData?.copy(
                                                    money = (userData?.money ?: 0) + (transaction.carPost?.price ?: 0)
                                                )
                                                if (updateUserMoney != null) {
                                                    googleAuthService.editUserProfile(
                                                        updatedUser = updateUserMoney,
                                                        image = null,
                                                        contextResolver = contentResolver
                                                    )
                                                }
                                                transactionRepository.getAllTransaction().collect { transaction ->
                                                    transactionData = transaction
                                                }
                                            }
                                            else {
                                                Toast.makeText(
                                                    applicationContext,
                                                    result.errorMessage,
                                                    Toast.LENGTH_LONG
                                                ).show()
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
}

