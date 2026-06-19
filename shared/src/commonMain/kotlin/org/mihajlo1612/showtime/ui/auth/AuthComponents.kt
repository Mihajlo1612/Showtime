package org.mihajlo1612.showtime.ui.auth

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mihajlo1612.showtime.ui.theme.ShowtimeColors

@Composable
fun ErrorCard(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, ShowtimeColors.ErrorRed, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("⊘", color = ShowtimeColors.ErrorRed, fontSize = 14.sp)
        Spacer(Modifier.width(8.dp))
        Text(message, color = ShowtimeColors.ErrorRed, fontSize = 13.sp)
    }
}

@Composable
fun showtimeTextFieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedBorderColor = ShowtimeColors.InputBorder,
    focusedBorderColor = ShowtimeColors.InputBorderFocused,
    unfocusedContainerColor = ShowtimeColors.InputBackground,
    focusedContainerColor = ShowtimeColors.InputBackground,
    unfocusedLabelColor = ShowtimeColors.InputLabel,
    focusedLabelColor = ShowtimeColors.InputBorderFocused,
    unfocusedTextColor = ShowtimeColors.TextPrimary,
    focusedTextColor = ShowtimeColors.TextPrimary,
    cursorColor = ShowtimeColors.PrimaryGold,
)