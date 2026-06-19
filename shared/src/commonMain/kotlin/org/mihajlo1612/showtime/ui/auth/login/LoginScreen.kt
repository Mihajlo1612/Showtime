package org.mihajlo1612.showtime.ui.auth.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.mihajlo1612.showtime.ui.auth.ErrorCard
import org.mihajlo1612.showtime.ui.auth.showtimeTextFieldColors
import org.mihajlo1612.showtime.ui.theme.ShowtimeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onBack: () -> Unit = {},
    viewModel: LoginViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                LoginSideEffect.NavigateToHome -> onNavigateToHome()
            }
        }
    }

    Scaffold(
        containerColor = ShowtimeColors.BackgroundPage,
        topBar = {
            TopAppBar(
                title = {
                    Text("Sign in", color = ShowtimeColors.TextPrimary, fontWeight = FontWeight.SemiBold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("❮", fontSize = 20.sp, color = ShowtimeColors.TextPrimary)                     }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ShowtimeColors.BackgroundPage),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
        ) {
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Welcome back",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = ShowtimeColors.TextPrimary,
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Sign in to continue to Showtime",
                fontSize = 14.sp,
                color = ShowtimeColors.TextSecondary,
            )

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = uiState.username,
                onValueChange = { viewModel.onEvent(LoginUiEvent.UsernameChanged(it)) },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = showtimeTextFieldColors(),
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.onEvent(LoginUiEvent.PasswordChanged(it)) },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = showtimeTextFieldColors(),
            )

            uiState.error?.let { error ->
                Spacer(Modifier.height(12.dp))
                ErrorCard(message = error)
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.onEvent(LoginUiEvent.LoginClicked) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ShowtimeColors.ButtonBackground,
                    contentColor = ShowtimeColors.ButtonText,
                    disabledContainerColor = ShowtimeColors.InputBorder,
                    disabledContentColor = ShowtimeColors.TextSecondary,
                ),
            ) {
                Text(
                    text = if (uiState.isLoading) "Signing in..." else "Sign in",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                )
            }
        }
    }
}