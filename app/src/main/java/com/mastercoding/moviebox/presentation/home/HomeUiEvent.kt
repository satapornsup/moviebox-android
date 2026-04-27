package com.mastercoding.moviebox.presentation.home

sealed interface HomeUiEvent {
    data class QueryChange(val q: String) : HomeUiEvent
    data object Retry : HomeUiEvent
}
