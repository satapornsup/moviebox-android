package com.mastercoding.moviebox.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeRoute(
    onMovieClick: (Int) -> Unit,
    onOpenFavorites: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onMovieClick = onMovieClick,
        onOpenFavorites = onOpenFavorites,
    )
}