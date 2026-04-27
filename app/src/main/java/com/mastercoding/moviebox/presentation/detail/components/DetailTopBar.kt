package com.mastercoding.moviebox.presentation.detail.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.mastercoding.moviebox.domain.model.Movie
import com.mastercoding.moviebox.presentation.detail.DetailUiEvent
import com.mastercoding.moviebox.presentation.detail.DetailUiState
import com.mastercoding.moviebox.presentation.home.MovieListProvider
import com.mastercoding.moviebox.ui.theme.MovieBoxTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopBar(
    state: DetailUiState,
    onEvent: (DetailUiEvent) -> Unit,
    onBack: () -> Unit,
) {
    TopAppBar(
        title = { Text(state.movie?.title ?: "Detail") },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(
                onClick = {
                    if (state.favoriteSaved) onEvent(DetailUiEvent.RemoveFavorite) else onEvent(
                        DetailUiEvent.AddFavorite
                    )
                },
                enabled = state.movie != null,
            ) {
                Icon(
                    imageVector = if (state.favoriteSaved) Icons.Filled.Favorite
                    else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (state.favoriteSaved) "Remove from favorites"
                    else "Add to favorites",
                    tint = if (state.favoriteSaved) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun DetailTopBarPreview(@PreviewParameter(MovieListProvider::class) movies: List<Movie>) {
    MovieBoxTheme {
        DetailTopBar(
            state = DetailUiState(movie = movies[0], favoriteSaved = false),
            onEvent = {},
            onBack = {},
        )
    }
}