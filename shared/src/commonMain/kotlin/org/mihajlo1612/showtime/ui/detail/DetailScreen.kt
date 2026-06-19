package org.mihajlo1612.showtime.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.mihajlo1612.showtime.domain.model.CastMember
import org.mihajlo1612.showtime.domain.model.MovieDetail
import org.mihajlo1612.showtime.ui.theme.ShowtimeColors

private const val TMDB_BASE = "https://image.tmdb.org/t/p/"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(imdbId: String, onBack: () -> Unit) {
    val viewModel: DetailViewModel = koinViewModel(parameters = { parametersOf(imdbId) })
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is DetailSideEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.text)
            }
        }
    }

    Scaffold(
        containerColor = ShowtimeColors.BackgroundPage,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "",
                        color = ShowtimeColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("❮", fontSize = 20.sp, color = ShowtimeColors.TextPrimary)                    }
                },

                actions = {
                    IconButton(onClick = { viewModel.onEvent(DetailUiEvent.ToggleWatchlist) }) {
                        Text(
                            text = if (uiState.isInWatchlist) "✓" else "＋",
                            color = ShowtimeColors.PrimaryGold,
                            fontSize = 22.sp,
                        )
                    }
                    IconButton(onClick = { viewModel.onEvent(DetailUiEvent.ToggleFavorite) }) {
                        Text(
                            text = if (uiState.isFavorite) "♥" else "♡",
                            color = ShowtimeColors.PrimaryGold,
                            fontSize = 22.sp,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ShowtimeColors.BackgroundPage,
                ),
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(ShowtimeColors.BackgroundPage),
        ) {
            when {
                uiState.isLoading && uiState.movie == null -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = ShowtimeColors.PrimaryGold,
                    )
                }
                uiState.movie != null -> {
                    DetailContent(movie = uiState.movie!!, cast = uiState.cast)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailContent(movie: MovieDetail, cast: List<CastMember>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(ShowtimeColors.BackgroundInput),
            ) {
                val backdropUrl = movie.backdropPath?.let { "${TMDB_BASE}w780$it" }
                    ?: movie.posterPath?.let { "${TMDB_BASE}w500$it" }
                if (backdropUrl != null) {
                    AsyncImage(
                        model = backdropUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                    )
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = movie.title,
                    color = ShowtimeColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                )

                Spacer(Modifier.height(6.dp))

                val meta = listOfNotNull(
                    movie.year?.toString(),
                    movie.runtime?.let { "${it}min" },
                ).joinToString(" · ")
                if (meta.isNotEmpty()) {
                    Text(text = meta, color = ShowtimeColors.TextSecondary, fontSize = 13.sp)
                }

                movie.imdbRating?.let { rating ->
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "★ $rating",
                        color = ShowtimeColors.PrimaryGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                }

                if (movie.genres.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        movie.genres.forEach { genre ->
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = ShowtimeColors.BackgroundInput,
                            ) {
                                Text(
                                    text = genre.name,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                    color = ShowtimeColors.TextSecondary,
                                    fontSize = 12.sp,
                                )
                            }
                        }
                    }
                }

                movie.overview?.let { overview ->
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Overview",
                        color = ShowtimeColors.TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = overview,
                        color = ShowtimeColors.TextSecondary,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                    )
                }

                if (cast.isNotEmpty()) {
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = "Cast",
                        color = ShowtimeColors.TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }

        if (cast.isNotEmpty()) {
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    items(cast, key = { it.imdbId }) { member ->
                        CastCard(member = member)
                    }
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun CastCard(member: CastMember) {
    Column(
        modifier = Modifier.width(72.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(ShowtimeColors.BackgroundInput),
            contentAlignment = Alignment.Center,
        ) {
            if (member.profilePath != null) {
                AsyncImage(
                    model = "${TMDB_BASE}w185${member.profilePath}",
                    contentDescription = member.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Text(text = "◯", color = ShowtimeColors.TextSecondary, fontSize = 26.sp)
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = member.name,
            color = ShowtimeColors.TextPrimary,
            fontSize = 11.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp,
        )
    }
}