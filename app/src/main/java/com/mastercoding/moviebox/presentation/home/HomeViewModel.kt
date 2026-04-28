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
    // ── pagination ──
    val page: Int = 0,
    val limit: Int = 10,
    val isLoadingMore: Boolean = false,
    val endReached: Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: MovieRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private var contentJob: Job? = null

    init {
        runLoadPopular()
    }

    fun onEvent(event: HomeUiEvent) = when (event) {
        is HomeUiEvent.QueryChange -> onQueryChange(event.q)
        HomeUiEvent.Retry -> retry()
        HomeUiEvent.LoadMore -> loadMore()
    }

    private fun runLoadPopular() {
        contentJob?.cancel()
        contentJob = viewModelScope.launch { loadPopular() }
    }

    private fun runDoSearch(q: String) {
        contentJob?.cancel()
        contentJob = viewModelScope.launch { doSearch(q) }
    }

    private fun onQueryChange(q: String) {
        _state.update { it.copy(query = q) }
        contentJob?.cancel()
        contentJob = viewModelScope.launch {
            delay(300) // debounce
            if (q.isBlank()) loadPopular() else doSearch(q)
        }
    }

    private fun retry() {
        val q = _state.value.query
        if (q.isBlank()) runLoadPopular() else runDoSearch(q)
    }

    private fun loadMore() {
        val s = _state.value
        if (s.isLoadingMore || s.endReached || s.loading) return
        if (s.query.isNotBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true, error = null) }
            runCatching { repo.popular(page = s.page + 1, limit = _state.value.limit) }
                .onSuccess { result ->
                    Log.d(
                        "HomeVM.loadMore",
                        "got page=${result.page} of totalPages=${result.totalPages}, items=${result.movies.size}"
                    )
                    _state.update {
                        it.copy(
                            isLoadingMore = false,
                            movies = it.movies + result.movies,
                            page = result.page,
                            endReached = result.page >= result.totalPages,
                        )
                    }
                }
                .onFailure { e ->
                    if (e is CancellationException) throw e
                    Log.e("HomeViewModel.loadMore", e.message ?: "")
                    _state.update { it.copy(isLoadingMore = false, error = e.message) }
                }
        }
    }

    private suspend fun loadPopular() {
        _state.update {
            it.copy(loading = true, error = null, page = 0, endReached = false)
        }
        runCatching { repo.popular(page = 1, limit = _state.value.limit) }
            .onSuccess { result ->
                Log.d(
                    "HomeViewModel.loadPopular",
                    "got page=${result.page} of totalPages=${result.totalPages}, items=${result.movies.size}"
                )
                _state.update {
                    it.copy(
                        loading = false,
                        movies = result.movies,
                        page = result.page,
                        endReached = result.page >= result.totalPages,
                    )
                }
            }
            .onFailure { e ->
                if (e is CancellationException) throw e
                Log.e("HomeViewModel.loadPopular", e.message ?: "")
                _state.update { it.copy(loading = false, error = e.message) }
            }
    }

    private suspend fun doSearch(q: String) {
        _state.update { it.copy(loading = true, error = null) }
        runCatching { repo.search(q) }
            .onSuccess { list ->
                _state.update {
                    // Search has no pagination → freeze pagination state so
                    // scroll-to-bottom won't trigger loadMore on results.
                    it.copy(
                        loading = false,
                        movies = list,
                        endReached = true,
                    )
                }
            }
            .onFailure { e ->
                if (e is CancellationException) throw e
                Log.e("HomeViewModel.doSearch", e.message ?: "")
                _state.update { it.copy(loading = false, error = e.message) }
            }
    }
}
