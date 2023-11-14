package com.example.car_rental_project.composable.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.*
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.car_rental_project.R
import com.example.car_rental_project.state.AuthState
import com.example.car_rental_project.view_model.SignInViewModel

@Composable
fun RegisterScreen(
    state: AuthState,
    viewModel : SignInViewModel,
    onSignUpClickWithGoogleTapiIn : () -> Unit,
    onSignUpClickWithEmail: (username : String?, email : String?, password : String?) -> Unit,
    onToLoginScreen : () -> Unit,
) {
    val context =  LocalContext.current
    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(Modifier.padding(32.dp, 16.dp)),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RegisterHeaderLogo()
        RegisterHeader(Modifier, onToLoginScreen)
        RegisterForm(
            onRegisterClick = { onSignUpClickWithEmail(state.email, state.password, state.username) },
            email = state.email,
            password = state.password,
            username = state.username,
            onEmailChange = { newEmail -> viewModel.onEmailChange(newEmail) },
            onPasswordChange = { newPassword -> viewModel.onPasswordChange(newPassword) },
            onUsernameChange = {newUsername -> viewModel.onUsernameChange(newUsername)}
        )
        Divider(color = Color.LightGray, thickness = 2.dp)
        LoginByGoogleButton(Modifier, onSignUpClickWithGoogleTapiIn)
    }
}

@Composable
fun RegisterHeader(modifier: Modifier = Modifier, onToLoginScreen: () -> Unit) {
    Column(
        modifier = modifier.fillMaxWidth().padding(0.dp, 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = R.string.register),
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
        )
        Text(
            text = stringResource(id = R.string.create_your_account),
            style = MaterialTheme.typography.titleSmall,
        )
        LoginLink(Modifier, onToLoginScreen)
    }
}

@Composable
fun RegisterHeaderLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.autohubicon),
        contentDescription = stringResource(id = R.string.login_header_logo),
        modifier = modifier
            .size(110.dp)
            .padding(top = 16.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterEmailTextInput(
    email: String?,
    onEmailChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        modifier = modifier,
        value = email.orEmpty(),
        onValueChange = { onEmailChange(it) },
        label = { Text(text = "Email") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPasswordTextInput(
    password: String?,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        modifier = modifier,
        value = password.orEmpty(),
        onValueChange = { onPasswordChange(it) },
        label = { Text(text = "Password") },
        visualTransformation = PasswordVisualTransformation()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterUsernameTextInput(
    username: String?,
    onUsernameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = lightColorScheme()

    TextField(
        value = username.orEmpty(),
        onValueChange = { onUsernameChange(it) },
        textStyle = TextStyle(color = colors.primary),
        modifier = modifier.background(colors.background),
        label = { Text(text = "Username") },
        )
}

@Composable
fun RegisterButton(onRegisterClick: () -> Unit) {
    Button(
        onClick = { onRegisterClick.invoke() },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(stringResource(id = R.string.sign_up))
    }
}

@Composable
fun LoginLink(modifier: Modifier = Modifier, onToLoginScreen: () -> Unit) {
    val text = stringResource(id = R.string.Already_have_an_account)
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color.Blue)) {
            append(text)
        }
    }

    ClickableText(
        text = annotatedString,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier,
        onClick = { onToLoginScreen.invoke() }
    )
}


@Composable
fun RegisterForm(
    modifier: Modifier = Modifier,
    onRegisterClick: () -> Unit,
    username : String?,
    email: String?,
    password: String?,
    onEmailChange: (String?) -> Unit,
    onPasswordChange: (String?) -> Unit,
    onUsernameChange: (String) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RegisterUsernameTextInput(
            username,
            onUsernameChange,
            modifier.fillMaxWidth()
        )
        RegisterEmailTextInput(
            email,
            onEmailChange,
            modifier.fillMaxWidth()
        )
        RegisterPasswordTextInput(
            password,
            onPasswordChange,
            modifier.fillMaxWidth()
        )
        RegisterButton(onRegisterClick)
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    val dummyViewModel = SignInViewModel()
    val dummyAuthState = AuthState(
        isSignInSuccessful = false,
        signInError = null,
    )
    RegisterScreen(
        viewModel = dummyViewModel,
        state = dummyAuthState,
        onSignUpClickWithGoogleTapiIn = {},
        onSignUpClickWithEmail = { email, password, username -> /* Define your click action here */ },
        onToLoginScreen = {}
    )
}