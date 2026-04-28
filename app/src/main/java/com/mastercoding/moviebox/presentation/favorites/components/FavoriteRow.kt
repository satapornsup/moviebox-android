package com.mastercoding.moviebox.presentation.favorites.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.mastercoding.moviebox.domain.model.Movie
import com.mastercoding.moviebox.presentation.home.MovieListProvider
import com.mastercoding.moviebox.presentation.home.components.MovieRow
import com.mastercoding.moviebox.ui.theme.MovieBoxTheme

@Composable
fun FavoriteRow(
    movie: Movie,
    onClick: (Int) -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.weight(1f)) {
            MovieRow(movie = movie, onClick = onClick)
        }
        IconButton(onClick = onRemove) {
            Icon(Icons.Filled.Delete, contentDescription = "Remove from favorites")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenPreview(
    @PreviewParameter(MovieListProvider::class) movies: List<Movie>,
) {
    MovieBoxTheme {
        FavoriteRow(
            movie = movies[0],
            onClick = {},
            onRemove = {},
        )
    }
}