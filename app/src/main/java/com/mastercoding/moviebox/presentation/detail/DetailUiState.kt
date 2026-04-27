package com.mastercoding.moviebox.presentation.detail

import com.mastercoding.moviebox.domain.model.Movie

data class DetailUiState(
    val loading: Boolean = false,
    val movie: Movie? = null,
    val error: String? = null,
    val favoriteSaved: Boolean = false,
)

sealed interface DetailUiEvent {
    data object Retry : DetailUiEvent
    data object AddFavorite : DetailUiEvent
    data object RemoveFavorite : DetailUiEvent
}
