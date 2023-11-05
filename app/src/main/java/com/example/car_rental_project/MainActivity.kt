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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.car_rental_project.composable.auth.LoginScreen
import com.example.car_rental_project.composable.auth.RegisterScreen
import com.example.car_rental_project.composable.home.HomeScreen
import com.example.car_rental_project.model.CarModel
import com.example.car_rental_project.service.GoogleAuthService
import com.example.car_rental_project.ui.theme.Car_Rental_ProjectTheme
import com.example.car_rental_project.view_model.SignInViewModel
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val googleAuthService by lazy {
        GoogleAuthService(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Car_Rental_ProjectTheme {

                val carList = listOf(
                    CarModel("1", "Toyota", "Corolla", "$10,000", R.drawable.placeholder_logo),
                    CarModel("2", "Honda", "Civic", "$9,500", R.drawable.placeholder_logo),
                    CarModel("3", "Ford", "Mustang", "$20,000", R.drawable.placeholder_logo),
                    CarModel("4", "Honda", "Brio", "$4,000", R.drawable.placeholder_logo),
                    CarModel("5", "Audi", "I8", "$34,000", R.drawable.placeholder_logo),
                    )

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                )
                {
                    val viewModel = viewModel<SignInViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    val navController = rememberNavController()

                    NavHost(navController, startDestination = "login") {
                        composable("login") {
                            LaunchedEffect(key1 = Unit) {
                                if(googleAuthService.getSignedInUser() != null) {
                                    navController.navigate("home")
                                }
                            }
                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    if(result.resultCode == RESULT_OK) {
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthService.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            viewModel.onSignInResult(signInResult)
                                        }
                                    }
                                    else {
                                        Log.d("onResult", "ActivityResult: " + result.toString())
                                    }
                                }
                            )
                            // login with email and password
                            LaunchedEffect(key1 = state.isSignInSuccessful) {
                                if(state.isSignInSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        R.string.Sign_in_Successful,
                                        Toast.LENGTH_LONG
                                    ).show()

                                    navController.navigate("home")
                                    viewModel.resetState()
                                }
                            }
                            LoginScreen(
                                viewModel = viewModel,
                                state = state,
                                onSignInClickWithGoogleTapiIn = {
                                    lifecycleScope.launch {
                                        val signInIntentSender = googleAuthService.signInWithGoogleOneTapClient()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntentSender ?: return@launch
                                            ).build()
                                        )
                                    }
                                },
                                onSignInClickWithEmail = { email, password ->
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthService.signInUserWithEmailAndPassword(email, password)
                                            viewModel.onSignInResult(signInResult)
                                        }
                                },
                                onToCreateAccountScreen = {
                                    navController.navigate("register")
                                    viewModel.resetState()
                                }

                            )
                }
                        composable("register") {
                            LaunchedEffect(key1 = Unit) {
                                if(googleAuthService.getSignedInUser() != null) {
                                    navController.navigate("home")
                                }
                            }
                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    if(result.resultCode == RESULT_OK) {
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthService.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            viewModel.onSignInResult(signInResult)
                                        }
                                    }
                                    else {
                                        Log.d("onResult", "ActivityResult: " + result.toString())
                                    }
                                }
                            )
                            LaunchedEffect(key1 = state.isSignInSuccessful) {
                                if(state.isSignInSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        R.string.Sign_in_Successful,
                                        Toast.LENGTH_LONG
                                    ).show()

                                    navController.navigate("home")
                                    viewModel.resetState()
                                }
                            }

                            RegisterScreen(
                                viewModel = viewModel,
                                state = state,
                                onSignUpClickWithGoogleTapiIn = {
                                    lifecycleScope.launch {
                                        val signInIntentSender = googleAuthService.signInWithGoogleOneTapClient()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntentSender ?: return@launch
                                            ).build()
                                        )
                                    }
                                },
                                onSignUpClickWithEmail = { email, password, username ->
                                    lifecycleScope.launch {
                                        val signInResult = googleAuthService.createUserWithEmailAndPassword(
                                            username = username,
                                            email = email,
                                            password = password)
                                        viewModel.onSignInResult(signInResult)
                                    }
                                },
                                onToLoginScreen = {
                                    navController.navigate("login")
                                    viewModel.resetState()
                                }

                            )
                        }
                        composable("home") {
                            HomeScreen(
                                userData = googleAuthService.getSignedInUser(),
                                onSignOut = {
                                    lifecycleScope.launch {
                                        googleAuthService.signOut()
                                        Toast.makeText(
                                            applicationContext,
                                            R.string.Signed_Out,
                                            Toast.LENGTH_LONG
                                        ).show()
                                        navController.popBackStack()
                                    }
                                },
                                carList = carList
                            )
                        }
            }
                }
            }
        }
    }
}

