package com.mastercoding.moviebox.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastercoding.moviebox.data.repo.MovieRepository
import com.mastercoding.moviebox.domain.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

data class HomeUiState(
    val loading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val query: String = "",
    val error: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: MovieRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadPopular()
    }

    fun onEvent(event: HomeUiEvent) = when (event) {
        is HomeUiEvent.QueryChange -> onQueryChange(event.q)
        HomeUiEvent.Retry -> retry()
    }

    private fun loadPopular() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            runCatching { repo.popular() }
                .onSuccess { list -> _state.update { it.copy(loading = false, movies = list) } }
                .onFailure { e ->
                    if (e is CancellationException) throw e  // re-throw cancell
                    Log.e("loadPopular", e.message ?: "")
                    _state.update { it.copy(loading = false, error = e.message) }
                }
        }
    }

    fun onQueryChange(q: String) {
        _state.update { it.copy(query = q) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // debounce
            if (q.isBlank()) loadPopular() else doSearch(q)
        }
    }

    fun retry() {
        if (_state.value.query.isBlank()) loadPopular() else doSearch(_state.value.query)
    }

    private fun doSearch(q: String) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            runCatching { repo.search(q) }
                .onSuccess { list -> _state.update { it.copy(loading = false, movies = list) } }
                .onFailure { e ->
                    if (e is CancellationException) throw e  // re-throw cancellation
                    Log.e("doSearch", e.message ?: "")
                    _state.update { it.copy(loading = false, error = e.message) }
                }
        }
    }
}
