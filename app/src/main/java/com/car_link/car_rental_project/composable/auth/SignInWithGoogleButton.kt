package com.car_link.car_rental_project.composable.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.car_link.car_rental_project.R

@Composable
fun LoginByGoogleButton(modifier: Modifier = Modifier, onRegisterClick: () -> Unit?) {
    Row(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.or_sign_in_with),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Button(
                onClick = { onRegisterClick?.invoke() },
                modifier = Modifier.fillMaxWidth(),
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
}
