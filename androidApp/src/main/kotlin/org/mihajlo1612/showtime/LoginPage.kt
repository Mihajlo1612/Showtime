package org.mihajlo1612.showtime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun LoginPage() {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = ShowtimeColors.BackgroundPage
    ) { padding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Showtime",
                    fontSize = 60.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ShowtimeColors.PrimaryRed
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Welcome back",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShowtimeColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Sign in to your account to continue",
                    fontSize = 14.sp,
                    color = ShowtimeColors.TextSubheading
                )
            }

            Spacer(Modifier.height(70.dp))

            Column (
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "EMAIL",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ShowtimeColors.TextLabel,
                    letterSpacing = 0.6.sp
                )

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = {
                        Text("john@example.com", color = ShowtimeColors.TextPlaceholder)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    textStyle = TextStyle(color = ShowtimeColors.TextPrimary),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = ShowtimeColors.InputBorder,
                        focusedBorderColor = ShowtimeColors.InputBorderFocused,
                        unfocusedContainerColor = ShowtimeColors.InputBackground,
                        focusedContainerColor = ShowtimeColors.InputBackgroundFocused,
                        cursorColor = ShowtimeColors.PrimaryRed,
                    )
                )

                Spacer(Modifier.height(16.dp))
                Text(
                    text = "PASSWORD",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ShowtimeColors.TextLabel,
                    letterSpacing = 0.6.sp
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = {
                        Text("••••••••", color = ShowtimeColors.TextPlaceholder)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    textStyle = TextStyle(color = ShowtimeColors.TextPrimary),
                    trailingIcon = {
                        TextButton(onClick = { passwordVisible = !passwordVisible }) {
                            Text(
                                text = if (passwordVisible) "Hide" else "Show",
                                color = ShowtimeColors.PrimaryRed,
                                fontSize = 12.sp
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = ShowtimeColors.InputBorder,
                        focusedBorderColor = ShowtimeColors.InputBorderFocused,
                        unfocusedContainerColor = ShowtimeColors.InputBackground,
                        focusedContainerColor = ShowtimeColors.InputBackgroundFocused,
                        cursorColor = ShowtimeColors.PrimaryRed,
                    )
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { },  // TODO: Data storing to database
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ShowtimeColors.PrimaryRed
                    )
                ) {
                    Text(
                        text = "Login",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShowtimeColors.TextPrimary
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Don't have an account?",
                        color = ShowtimeColors.TextRegister,
                        fontSize = 13.sp
                    )

                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = "Register",
                        color = ShowtimeColors.PrimaryRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}