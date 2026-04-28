package com.mastercoding.moviebox.presentation.favorites

import com.mastercoding.moviebox.domain.model.Movie

data class FavoritesUiState(
    val loading: Boolean = false,
    val favorites: List<Movie> = emptyList(),
    val error: String? = null,
)

sealed interface FavoritesUiEvent {
    data object Retry : FavoritesUiEvent
    data class Remove(val movieId: Int) : FavoritesUiEvent
}
