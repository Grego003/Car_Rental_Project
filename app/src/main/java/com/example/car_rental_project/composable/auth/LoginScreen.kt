package com.example.car_rental_project.composable.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.car_rental_project.R
import com.example.car_rental_project.state.AuthState
import com.example.car_rental_project.view_model.SignInViewModel

@Composable
fun LoginScreen(
    state: AuthState,
    viewModel : SignInViewModel,
    onSignInClickWithGoogleTapiIn : () -> Unit,
    onSignInClickWithEmail: (email : String?, password : String?) -> Unit,
    onToCreateAccountScreen : ()->Unit,
    )
    {
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
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Red)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(Modifier.padding(32.dp, 16.dp)),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginHeaderLogo()
            LoginHeader(Modifier, onToCreateAccountScreen)
            LoginForm(
                onLoginClick = { onSignInClickWithEmail(state.email, state.password) },
                email = state.email,
                password = state.password,
                onEmailChange = { newEmail -> viewModel.onEmailChange(newEmail) },
                onPasswordChange = { newPassword -> viewModel.onPasswordChange(newPassword) }
            )
            Divider(color = Color.LightGray, thickness = 2.dp)
            LoginByGoogleButton(Modifier, onSignInClickWithGoogleTapiIn)
        }
    }
}

@Composable
fun LoginHeader(modifier: Modifier = Modifier, onToCreateAccountScreen: () -> Unit) {
    Column(
        modifier = modifier.fillMaxWidth().padding(0.dp, 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = R.string.login),
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
        )
        Text(
            text = stringResource(id = R.string.sign_in_to_continue),
            style = MaterialTheme.typography.titleMedium,
        )
        RegisterLink(Modifier, onToCreateAccountScreen)
    }
}

@Composable
fun LoginHeaderLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.logo_autohub),
        contentDescription = stringResource(id = R.string.login_header_logo),
        modifier = modifier.size(100.dp)
    )
}

@Composable
fun RegisterLink(modifier: Modifier = Modifier, onToCreateAccountScreen: () -> Unit) {
    val text = stringResource(id = R.string.sign_up_here)
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color.Blue)) {
            append(text)
        }
    }

    ClickableText(
        text = annotatedString,
        style = MaterialTheme.typography.labelMedium,
        modifier = modifier,
        onClick = {onToCreateAccountScreen.invoke() }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginEmailTextInput(
    email: String ?,
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
fun LoginPasswordTextInput(
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

@Composable
fun LoginForm(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit,
    email: String?,
    password: String?,
    onEmailChange: (String?) -> Unit,
    onPasswordChange: (String?) -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoginEmailTextInput(
            email,
            onEmailChange,
            modifier.fillMaxWidth()
        )
        LoginPasswordTextInput(
            password,
            onPasswordChange,
            modifier.fillMaxWidth()
        )
        SubmitButton(onLoginClick)
    }
}

@Composable
fun SubmitButton(onLoginClick: () -> Unit) {
    Button(
        onClick = { onLoginClick.invoke() },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(stringResource(id = R.string.sign_in))
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val dummyViewModel = SignInViewModel()
    val dummyAuthState = AuthState(
        isSignInSuccessful = false,
        signInError = null,
    )
    LoginScreen(
        dummyAuthState,
        dummyViewModel,
        onSignInClickWithEmail = { email, password -> /* Define your click action here */ },
        onSignInClickWithGoogleTapiIn = {},
        onToCreateAccountScreen = {})
}



