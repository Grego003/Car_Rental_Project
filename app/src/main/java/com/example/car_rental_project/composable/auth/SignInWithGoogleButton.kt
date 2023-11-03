package com.example.car_rental_project.composable.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.car_rental_project.R

@Composable
fun LoginByGoogleButton(modifier: Modifier = Modifier, onRegisterClick: () -> Unit?) {
    Row(modifier = modifier) {
        Button(
            onClick = { onRegisterClick.invoke() },
            modifier = modifier,
        ) {
            Image(
                painter = painterResource(id = R.drawable._123025_logo_google_g_icon),
                contentDescription = stringResource(id = R.string.google_logo),
                modifier = Modifier.size(50.dp)
            )
            Text(stringResource(id = R.string.sign_in_with_google))
        }
    }
}