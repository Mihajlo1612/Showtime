package org.mihajlo1612.showtime.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.mihajlo1612.showtime.ui.theme.ShowtimeColors

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    val viewModel: ProfileViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShowtimeColors.BackgroundPage)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(ShowtimeColors.BackgroundInput),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = state.fullName.take(1).uppercase().ifEmpty { "?" },
                color = ShowtimeColors.PrimaryGold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
            )
        }

        Spacer(Modifier.height(16.dp))
        Text(state.fullName, color = ShowtimeColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("@${state.username}", color = ShowtimeColors.TextSecondary, fontSize = 14.sp)

        Spacer(Modifier.height(28.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Best score", "${state.bestScore.toInt()}", Modifier.weight(1f))
            StatCard("Quizzes", "${state.gamesPlayed}", Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Favorites", "${state.favoritesCount}", Modifier.weight(1f))
            StatCard("Watchlist", "${state.watchlistCount}", Modifier.weight(1f))
        }

        Spacer(Modifier.weight(1f))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = ShowtimeColors.BackgroundInput,
            modifier = Modifier.fillMaxWidth().clickable(onClick = onLogout),
        ) {
            Text(
                text = "Logout",
                modifier = Modifier.padding(vertical = 14.dp).fillMaxWidth(),
                color = ShowtimeColors.ErrorRed,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(shape = RoundedCornerShape(12.dp), color = ShowtimeColors.BackgroundInput, modifier = modifier) {
        Column(Modifier.padding(vertical = 18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = ShowtimeColors.PrimaryGold, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
            Text(label, color = ShowtimeColors.TextSecondary, fontSize = 12.sp)
        }
    }
}