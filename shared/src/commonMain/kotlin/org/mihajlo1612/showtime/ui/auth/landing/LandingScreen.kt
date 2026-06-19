package org.mihajlo1612.showtime.ui.auth.landing

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mihajlo1612.showtime.ui.theme.ShowtimeColors

@Composable
fun LandingScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShowtimeColors.BackgroundPage)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))

        Text(text = "🎬", fontSize = 52.sp)

        Spacer(Modifier.height(16.dp))

        Text(
            text = "SHOWTIME",
            fontSize = 34.sp,
            fontWeight = FontWeight.ExtraBold,
            color = ShowtimeColors.PrimaryGold,
            letterSpacing = 4.sp,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Your personal movie universe",
            fontSize = 14.sp,
            color = ShowtimeColors.TextSecondary,
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = "Discover, track, and quiz your movie knowledge",
            fontSize = 13.sp,
            color = ShowtimeColors.TextSecondary,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onNavigateToLogin,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ShowtimeColors.ButtonBackground,
                contentColor = ShowtimeColors.ButtonText,
            ),
        ) {
            Text("Sign in", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onNavigateToRegister,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF3A3A5C)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = ShowtimeColors.TextPrimary,
            ),
        ) {
            Text("Create account", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Syncs favorites & watchlist across devices",
            fontSize = 12.sp,
            color = ShowtimeColors.TextHint,
        )

        Spacer(Modifier.height(40.dp))
    }
}