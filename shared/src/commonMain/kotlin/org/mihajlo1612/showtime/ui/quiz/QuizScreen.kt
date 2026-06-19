package org.mihajlo1612.showtime.ui.quiz

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel
import org.mihajlo1612.showtime.domain.model.QuizQuestion
import org.mihajlo1612.showtime.ui.theme.ShowtimeColors

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Suppress("DEPRECATION")
fun QuizScreen(onBackToCatalog: () -> Unit) {
    val viewModel: QuizViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showAbandon by remember { mutableStateOf(false) }

    BackHandler(enabled = state.phase == QuizPhase.Playing) {
        showAbandon = true
    }

    Box(Modifier.fillMaxSize().background(ShowtimeColors.BackgroundPage)) {
        when (state.phase) {
            QuizPhase.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = ShowtimeColors.PrimaryGold,
            )
            QuizPhase.NotEnough -> Text(
                text = "Browse the catalog first to populate your quiz pool.",
                modifier = Modifier.align(Alignment.Center).padding(32.dp),
                color = ShowtimeColors.TextSecondary,
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
            )
            QuizPhase.Playing -> PlayingContent(state, viewModel::onEvent)
            QuizPhase.Finished -> ResultContent(state, viewModel::onEvent, onBackToCatalog)
        }
    }

    if (showAbandon) {
        AlertDialog(
            onDismissRequest = { showAbandon = false },
            containerColor = ShowtimeColors.BackgroundInput,
            title = { Text("Abandon quiz?", color = ShowtimeColors.TextPrimary) },
            text = { Text("Your progress will be lost.", color = ShowtimeColors.TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    showAbandon = false
                    viewModel.onEvent(QuizUiEvent.Abandon)
                    onBackToCatalog()
                }) { Text("Abandon", color = ShowtimeColors.ErrorRed) }
            },
            dismissButton = {
                TextButton(onClick = { showAbandon = false }) {
                    Text("Cancel", color = ShowtimeColors.TextSecondary)
                }
            },
        )
    }
}

private fun formatTime(s: Int): String = "${s / 60}:${(s % 60).toString().padStart(2, '0')}"

@Composable
private fun PlayingContent(state: QuizUiState, onEvent: (QuizUiEvent) -> Unit) {
    if (state.current == null) return

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Quiz · Q${state.currentIndex + 1}/${state.totalQuestions}",
                color = ShowtimeColors.TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = formatTime(state.remainingSeconds),
                color = ShowtimeColors.PrimaryGold,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
        }

        Spacer(Modifier.height(10.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(state.totalQuestions) { i ->
                Box(
                    Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (i <= state.currentIndex) ShowtimeColors.PrimaryGold
                            else ShowtimeColors.InputBorder
                        )
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        AnimatedContent(
            targetState = state.currentIndex,
            transitionSpec = {
                (slideInHorizontally { it } + fadeIn()) togetherWith
                        (slideOutHorizontally { -it } + fadeOut())
            },
            label = "question",
        ) { index ->
            val q = state.questions.getOrNull(index) ?: return@AnimatedContent
            Column {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 10f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ShowtimeColors.BackgroundInput),
                ) {
                    AsyncImage(
                        model = q.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                    Box(
                        Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .background(Color(0xCC0A0A14))
                            .padding(12.dp),
                    ) {
                        Text(text = q.prompt, color = ShowtimeColors.TextPrimary, fontSize = 14.sp)
                    }
                }

                q.title?.let {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = it,
                        color = ShowtimeColors.TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "CHOOSE AN ANSWER",
                    color = ShowtimeColors.TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(Modifier.height(8.dp))

                q.options.forEach { option ->
                    AnswerRow(
                        option = option,
                        state = state,
                        q = q,
                        onClick = { onEvent(QuizUiEvent.AnswerSelected(option)) },
                    )
                    Spacer(Modifier.height(10.dp))
                }
            }
        }

        Spacer(Modifier.weight(1f))

        if (state.answered) {
            Text(
                text = "Next question in 2s…",
                color = ShowtimeColors.TextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun AnswerRow(
    option: String,
    state: QuizUiState,
    q: QuizQuestion,
    onClick: () -> Unit,
) {
    val isCorrect = option == q.correctAnswer
    val isPicked = option == state.selectedAnswer
    val borderColor = when {
        !state.answered -> ShowtimeColors.InputBorder
        isCorrect -> ShowtimeColors.SuccessGreen
        isPicked -> ShowtimeColors.ErrorRed
        else -> ShowtimeColors.InputBorder
    }
    val textColor = when {
        !state.answered -> ShowtimeColors.TextPrimary
        isCorrect -> ShowtimeColors.SuccessGreen
        isPicked -> ShowtimeColors.ErrorRed
        else -> ShowtimeColors.TextSecondary
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = ShowtimeColors.BackgroundInput,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(enabled = !state.answered, onClick = onClick),
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (state.answered && (isCorrect || isPicked)) {
                Text(
                    text = if (isCorrect) "✓" else "✗",
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(end = 10.dp),
                )
            }
            Text(text = option, color = textColor, fontSize = 15.sp)
        }
    }
}

@Composable
private fun ResultContent(
    state: QuizUiState,
    onEvent: (QuizUiEvent) -> Unit,
    onBackToCatalog: () -> Unit,
) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(20.dp))
        Text("Quiz result", color = ShowtimeColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 22.sp)

        Spacer(Modifier.height(28.dp))

        Box(
            Modifier.size(150.dp).clip(CircleShape)
                .border(4.dp, ShowtimeColors.PrimaryGold, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formatScore(state.finalScore),
                    color = ShowtimeColors.PrimaryGold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 34.sp,
                )
                Text("points", color = ShowtimeColors.TextSecondary, fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = "${state.correctCount} correct · ${state.wrongCount} wrong · ${state.timeUsed}s used",
            color = ShowtimeColors.TextSecondary,
            fontSize = 14.sp,
        )

        Spacer(Modifier.height(24.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Correct", "${state.correctCount}", Modifier.weight(1f))
            StatCard("Wrong", "${state.wrongCount}", Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Time used", "${state.timeUsed}s", Modifier.weight(1f))
            StatCard("Remaining", "${state.remainingSeconds}s", Modifier.weight(1f))
        }

        state.ranking?.let {
            Spacer(Modifier.height(12.dp))
            Text("Leaderboard rank: #$it", color = ShowtimeColors.PrimaryGold, fontSize = 18.sp)
        }

        state.submitError?.let {
            Spacer(Modifier.height(12.dp))
            Text("⚠ $it", color = ShowtimeColors.ErrorRed, fontSize = 12.sp, textAlign = TextAlign.Center)
        }

        Spacer(Modifier.height(28.dp))

        ResultButton("Play again", filled = true) { onEvent(QuizUiEvent.Restart) }
        Spacer(Modifier.height(12.dp))
        ResultButton("Back to catalog", filled = false, onClick = onBackToCatalog)
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

@Composable
private fun ResultButton(text: String, filled: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (filled) ShowtimeColors.PrimaryGold else ShowtimeColors.BackgroundInput,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(vertical = 14.dp).fillMaxWidth(),
            color = if (filled) ShowtimeColors.ButtonText else ShowtimeColors.TextPrimary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

private fun formatScore(score: Float): String {
    val rounded = (score * 10).toInt() / 10.0  // 1 decimala
    return rounded.toString()
}