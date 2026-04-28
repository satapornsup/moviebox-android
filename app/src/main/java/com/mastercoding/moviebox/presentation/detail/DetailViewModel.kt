package com.mastercoding.moviebox.presentation.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastercoding.moviebox.data.repo.FavoriteRepository
import com.mastercoding.moviebox.data.repo.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val movieRepo: MovieRepository,
    private val favoriteRepo: FavoriteRepository,
) : ViewModel() {

    private val movieId: Int = checkNotNull(savedStateHandle["id"])

    private val _state = MutableStateFlow(DetailUiState())
    val state: StateFlow<DetailUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun onEvent(event: DetailUiEvent) = when (event) {
        DetailUiEvent.Retry -> load()
        DetailUiEvent.AddFavorite -> addFavorite()
        DetailUiEvent.RemoveFavorite -> removeFavorite()
    }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            runCatching {
                coroutineScope {
                    val movieAsync = async { movieRepo.detail(movieId) }
                    val isFavAsync = async {
                        runCatching { favoriteRepo.list() }
                            .getOrDefault(emptyList())
                            .any { it.id == movieId }
                    }
                    movieAsync.await() to isFavAsync.await()
                }
            }
                .onSuccess { (movie, isFav) ->
                    _state.update {
                        it.copy(loading = false, movie = movie, favoriteSaved = isFav)
                    }
                }
                .onFailure { e ->
                    if (e is CancellationException) throw e
                    Log.e("DetailViewModel.load", e.message ?: "")
                    _state.update { it.copy(loading = false, error = e.message) }
                }
        }
    }

    private fun addFavorite() {
        val current = _state.value.movie ?: return
        viewModelScope.launch {
            runCatching { favoriteRepo.add(current) }
                .onSuccess {
                    _state.update { it.copy(favoriteSaved = true) }
                }
                .onFailure { e ->
                    if (e is CancellationException) throw e
                    Log.e("DetailViewModel.addFavorite", e.message ?: "")
                    _state.update { it.copy(error = e.message) }
                }
        }
    }

    private fun removeFavorite() {
        val current = _state.value.movie ?: return
        viewModelScope.launch {
            runCatching { favoriteRepo.remove(current.id) }
                .onSuccess {
                    _state.update { it.copy(favoriteSaved = false) }
                }
                .onFailure { e ->
                    if (e is CancellationException) throw e
                    Log.e("DetailViewModel.removeFavorite", e.message ?: "")
                    _state.update { it.copy(error = e.message) }
                }
        }
    }
}
