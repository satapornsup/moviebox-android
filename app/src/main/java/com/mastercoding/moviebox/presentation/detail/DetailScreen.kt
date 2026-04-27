package com.mastercoding.moviebox.presentation.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.mastercoding.moviebox.domain.model.Movie
import com.mastercoding.moviebox.core.ui.ErrorView
import com.mastercoding.moviebox.presentation.detail.components.DetailTopBar
import com.mastercoding.moviebox.presentation.detail.components.MovieDetailContent
import com.mastercoding.moviebox.presentation.home.MovieListProvider
import com.mastercoding.moviebox.ui.theme.MovieBoxTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    state: DetailUiState,
    onEvent: (DetailUiEvent) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = { DetailTopBar(state, onEvent, onBack) }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.loading ->
                    CircularProgressIndicator(Modifier.align(Alignment.Center))

                state.error != null ->
                    ErrorView(
                        message = state.error,
                        onRetry = { onEvent(DetailUiEvent.Retry) },
                        modifier = Modifier.align(Alignment.Center),
                    )

                state.movie != null ->
                    MovieDetailContent(
                        movie = state.movie,
                        favoriteSaved = state.favoriteSaved,
                    )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailScreenPreview(@PreviewParameter(MovieListProvider::class) movies: List<Movie>) {
    MovieBoxTheme {
        DetailScreen(
            state = DetailUiState(
                movie = movies[0],
                favoriteSaved = true,
            ),
            onEvent = {},
            onBack = {},
        )
    }
}
