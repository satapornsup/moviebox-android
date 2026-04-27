package com.mastercoding.moviebox.presentation.favorites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect

@Composable
fun FavoritesRoute(
    onMovieClick: (Int) -> Unit,
    onBack: () -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val state by viewModel.state.observeAsState(FavoritesUiState())

    LifecycleResumeEffect(Unit) {
        viewModel.refresh()
        onPauseOrDispose { }
    }

    FavoritesScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onMovieClick = onMovieClick,
        onBack = onBack,
    )
}
