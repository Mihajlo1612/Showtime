package org.mihajlo1612.showtime.ui.catalog

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.koin.compose.viewmodel.koinViewModel
import org.mihajlo1612.showtime.domain.model.Genre
import org.mihajlo1612.showtime.domain.model.Movie
import org.mihajlo1612.showtime.ui.auth.ErrorCard
import org.mihajlo1612.showtime.ui.theme.ShowtimeColors

private val genreChipColors = listOf(
    Color(0xFF1E3A5F),
    Color(0xFF2D4A3E),
    Color(0xFF4A2040),
    Color(0xFF3A2D5F),
    Color(0xFF1E4A4A),
    Color(0xFF4A3020),
)

@Composable
fun CatalogScreen(onNavigateToDetail: (String) -> Unit) {
    val viewModel: CatalogViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is CatalogSideEffect.NavigateToDetail -> onNavigateToDetail(effect.imdbId)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShowtimeColors.BackgroundPage),
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = "Showtime",
                color = ShowtimeColors.TextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
            )

            Spacer(Modifier.height(12.dp))

            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onEvent(CatalogUiEvent.SearchChanged(it)) },
            )

            Spacer(Modifier.height(12.dp))
        }

        GenreFilterRow(
            genres = uiState.genres,
            selectedGenreId = uiState.selectedGenreId,
            onGenreSelected = { viewModel.onEvent(CatalogUiEvent.GenreSelected(it)) },
        )

        Spacer(Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading && uiState.allMovies.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = ShowtimeColors.PrimaryGold,
                    )
                }
                uiState.error != null && uiState.allMovies.isEmpty() -> {
                    Box(
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    ) {
                        ErrorCard(message = uiState.error!!)
                    }
                }
                else -> {
                    MovieList(
                        movies = uiState.displayedMovies,
                        isLoadingMore = uiState.isLoading,
                        onMovieClick = { viewModel.onEvent(CatalogUiEvent.MovieClicked(it)) },
                        onLoadMore = { viewModel.onEvent(CatalogUiEvent.LoadMore) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ShowtimeColors.BackgroundInput)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "⌕", color = ShowtimeColors.TextSecondary, fontSize = 18.sp)
            Spacer(Modifier.width(10.dp))
            Box {
                if (query.isEmpty()) {
                    Text(
                        text = "Search movies...",
                        color = ShowtimeColors.TextHint,
                        fontSize = 14.sp,
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    cursorBrush = SolidColor(ShowtimeColors.PrimaryGold),
                    textStyle = TextStyle(
                        color = ShowtimeColors.TextPrimary,
                        fontSize = 14.sp,
                    ),
                )
            }
        }
    }
}

@Composable
private fun GenreFilterRow(
    genres: List<Genre>,
    selectedGenreId: Int?,
    onGenreSelected: (Int?) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            GenreChip(
                label = "All",
                selected = selectedGenreId == null,
                onClick = { onGenreSelected(null) },
            )
        }
        items(genres, key = { it.id }) { genre ->
            GenreChip(
                label = genre.name,
                selected = selectedGenreId == genre.id,
                onClick = { onGenreSelected(genre.id) },
            )
        }
    }
}

@Composable
private fun GenreChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(25.dp),
        color = if (selected) ShowtimeColors.PrimaryGold else ShowtimeColors.BackgroundInput,
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
            color = if (selected) ShowtimeColors.ButtonText else ShowtimeColors.TextSecondary,
            fontSize = 18.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

@Composable
private fun MovieList(
    movies: List<Movie>,
    isLoadingMore: Boolean,
    onMovieClick: (String) -> Unit,
    onLoadMore: () -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            total > 0 && last >= total - 4
        }
            .distinctUntilChanged()
            .filter { it }
            .collect { onLoadMore() }
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(movies, key = { it.imdbId }) { movie ->
            MovieCard(movie = movie, onClick = { onMovieClick(movie.imdbId) })
        }
        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = ShowtimeColors.PrimaryGold,
                        strokeWidth = 2.dp,
                    )
                }
            }
        }
    }
}

@Composable
private fun MovieCard(movie: Movie, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ShowtimeColors.BackgroundInput),
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(width = 80.dp, height = 130.dp)
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

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    color = ShowtimeColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(Modifier.height(4.dp))

                val meta = listOfNotNull(
                    movie.year?.toString(),
                ).joinToString(" · ")
                if (meta.isNotEmpty()) {
                    Text(text = meta, color = ShowtimeColors.TextSecondary, fontSize = 14.sp)
                }

                movie.imdbRating?.let { rating ->
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "★ $rating",
                        color = ShowtimeColors.PrimaryGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                if (movie.genres.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        movie.genres.take(3).forEachIndexed { index, genre ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = genreChipColors[genre.id % genreChipColors.size],
                            ) {
                                Text(
                                    text = genre.name,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    color = ShowtimeColors.TextPrimary,
                                    fontSize = 12.sp,
                                )
                            }
                        }
                    }
                }

            }
        }
    }
}