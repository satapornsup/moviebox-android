package com.mastercoding.moviebox.presentation.favorites

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastercoding.moviebox.data.repo.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteRepo: FavoriteRepository,
) : ViewModel() {

    private val _state = MutableLiveData(FavoritesUiState(loading = true))
    val state: LiveData<FavoritesUiState> = _state

    fun onEvent(event: FavoritesUiEvent) = when (event) {
        FavoritesUiEvent.Retry -> load()
        is FavoritesUiEvent.Remove -> remove(event.movieId)
    }

    fun refresh() = load()

    private fun load() {
        viewModelScope.launch {
            _state.value = current().copy(loading = true, error = null)
            runCatching { favoriteRepo.list() }
                .onSuccess { list ->
                    _state.value = current().copy(loading = false, favorites = list)
                }
                .onFailure { e ->
                    if (e is CancellationException) throw e
                    Log.e("FavoritesViewModel.load", e.message ?: "")
                    _state.value = current().copy(loading = false, error = e.message)
                }
        }
    }

    private fun remove(movieId: Int) {
        viewModelScope.launch {
            val before = current().favorites
            _state.value = current().copy(favorites = before.filterNot { it.id == movieId })

            runCatching { favoriteRepo.remove(movieId) }
                .onFailure { e ->
                    if (e is CancellationException) throw e
                    Log.e("FavoritesViewModel.remove", e.message ?: "")
                    _state.value = current().copy(favorites = before, error = e.message)
                }
        }
    }

    private fun current(): FavoritesUiState = _state.value ?: FavoritesUiState()
}
