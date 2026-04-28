package com.mastercoding.moviebox.presentation.favorites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.mastercoding.moviebox.core.ui.ErrorView
import com.mastercoding.moviebox.domain.model.Movie
import com.mastercoding.moviebox.presentation.favorites.components.FavoriteRow
import com.mastercoding.moviebox.presentation.favorites.components.FavoritesTopBar
import com.mastercoding.moviebox.presentation.home.MovieListProvider
import com.mastercoding.moviebox.ui.theme.MovieBoxTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    state: FavoritesUiState,
    onEvent: (FavoritesUiEvent) -> Unit,
    onMovieClick: (Int) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            FavoritesTopBar(onBack)
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.loading && state.favorites.isEmpty() ->
                    CircularProgressIndicator(Modifier.align(Alignment.Center))

                state.error != null && state.favorites.isEmpty() ->
                    ErrorView(
                        message = state.error,
                        onRetry = { onEvent(FavoritesUiEvent.Retry) },
                        modifier = Modifier.align(Alignment.Center),
                    )

                state.favorites.isEmpty() ->
                    Text(
                        text = "No favorites yet.\nTap the heart on a movie to save it.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                    )

                else ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                    ) {
                        items(items = state.favorites, key = { it.id }) { movie ->
                            FavoriteRow(
                                movie = movie,
                                onClick = onMovieClick,
                                onRemove = { onEvent(FavoritesUiEvent.Remove(movie.id)) },
                            )
                        }
                    }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenPreview(
    @PreviewParameter(MovieListProvider::class) movies: List<Movie>,
) {
    MovieBoxTheme {
        FavoritesScreen(
            state = FavoritesUiState(favorites = movies),
            onEvent = {},
            onMovieClick = {},
            onBack = {},
        )
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
private fun FavoritesScreenEmptyPreview() {
    MovieBoxTheme {
        FavoritesScreen(
            state = FavoritesUiState(favorites = emptyList()),
            onEvent = {},
            onMovieClick = {},
            onBack = {},
        )
    }
}
