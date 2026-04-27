package com.mastercoding.moviebox.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.mastercoding.moviebox.domain.model.Movie
import com.mastercoding.moviebox.presentation.home.MovieListProvider

@Composable
fun MovieList(
    movies: List<Movie>,
    onClick: (Int) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(vertical = 4.dp),
    ) {
        items(movies, key = { it.id }) { m -> MovieRow(m, onClick) }
    }
}

@Preview(showBackground = true)
@Composable
private fun MovieListPreview(@PreviewParameter(MovieListProvider::class) movies: List<Movie>) {
    MovieList(movies = movies, onClick = {})
}