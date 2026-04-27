package com.mastercoding.moviebox.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.mastercoding.moviebox.domain.model.Movie
import com.mastercoding.moviebox.core.ui.ErrorView
import com.mastercoding.moviebox.presentation.home.components.MovieList
import com.mastercoding.moviebox.ui.theme.MovieBoxTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    onEvent: (HomeUiEvent) -> Unit,
    onMovieClick: (Int) -> Unit,
    onOpenFavorites: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MovieBox") },
                actions = {
                    Button(onClick = onOpenFavorites) { Text("Favorites") }
                    Spacer(Modifier.width(8.dp))
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = { onEvent(HomeUiEvent.QueryChange(it)) },
                label = { Text("Search movies") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            Box(Modifier.fillMaxSize()) {
                when {
                    state.loading && state.movies.isEmpty() ->
                        CircularProgressIndicator(Modifier.align(Alignment.Center))

                    state.error != null ->
                        ErrorView(
                            state.error,
                            onRetry = { onEvent(HomeUiEvent.Retry) },
                            Modifier.align(Alignment.Center)
                        )

                    state.movies.isEmpty() ->
                        Text("No results", Modifier.align(Alignment.Center))

                    else -> MovieList(state.movies, onMovieClick)
                }
            }
        }
    }
}

@Preview
@Composable
private fun MovieRowPreview(@PreviewParameter(MovieListProvider::class) movies: List<Movie>) {
    MovieBoxTheme {
        HomeScreen(
            state = HomeUiState(
                movies = movies
            ),
            onEvent = {},
            onMovieClick = {},
            onOpenFavorites = {},
        )
    }
}