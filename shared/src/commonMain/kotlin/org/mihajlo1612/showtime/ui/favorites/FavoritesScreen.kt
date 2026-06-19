package org.mihajlo1612.showtime.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel
import org.mihajlo1612.showtime.domain.model.Movie
import org.mihajlo1612.showtime.ui.theme.ShowtimeColors

@Composable
fun FavoritesScreen(onNavigateToDetail: (String) -> Unit) {
    val viewModel: FavoritesViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is FavoritesSideEffect.NavigateToDetail -> onNavigateToDetail(effect.imdbId)
                else -> {}
            }
        }
    }

    Column(Modifier.fillMaxSize().background(ShowtimeColors.BackgroundPage)) {
        Text(
            text = "Favorites",
            color = ShowtimeColors.TextPrimary,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp),
        )

        if (state.movies.isEmpty() && !state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No favorites yet.\nTap the ♥ on a movie to add it here.",
                    color = ShowtimeColors.TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(state.movies, key = { it.imdbId }) { movie ->
                    FavoriteCard(
                        movie = movie,
                        onClick = { viewModel.onEvent(FavoritesUiEvent.MovieClicked(movie.imdbId)) },
                        onRemove = { viewModel.onEvent(FavoritesUiEvent.RemoveClicked(movie.imdbId)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteCard(movie: Movie, onClick: () -> Unit, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ShowtimeColors.BackgroundInput),
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(width = 56.dp, height = 80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ShowtimeColors.InputBorder),
            ) {
                if (movie.posterPath != null) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w185${movie.posterPath}",
                        contentDescription = movie.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    color = ShowtimeColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                movie.year?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(text = "$it", color = ShowtimeColors.TextSecondary, fontSize = 12.sp)
                }
            }

            Text(
                text = "♥",
                color = ShowtimeColors.PrimaryGold,
                fontSize = 22.sp,
                modifier = Modifier.clickable(onClick = onRemove).padding(8.dp),
            )
        }
    }
}