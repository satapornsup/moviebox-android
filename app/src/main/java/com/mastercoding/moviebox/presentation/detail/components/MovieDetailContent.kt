package com.mastercoding.moviebox.presentation.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mastercoding.moviebox.R
import com.mastercoding.moviebox.domain.model.Movie
import com.mastercoding.moviebox.presentation.home.MovieListProvider
import com.mastercoding.moviebox.ui.theme.MovieBoxTheme

@Composable
fun MovieDetailContent(
    movie: Movie,
    favoriteSaved: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {

        AsyncImage(
            model = movie.backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" }
                ?: movie.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
            contentDescription = movie.title,
            placeholder = painterResource(R.drawable.outline_image_24),
            error = painterResource(R.drawable.outline_broken_image_24),
            fallback = painterResource(R.drawable.outline_image_24),
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
        )

        Text(
            text = movie.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )

        MetaRow(
            release = movie.releaseDate,
            rating = movie.voteAverage,
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Overview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = movie.overview.ifBlank { "No overview available." },
            style = MaterialTheme.typography.bodyMedium,
        )

        if (favoriteSaved) {
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Saved to Favorites")
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Saved",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MovieDetailContentPreview(@PreviewParameter(MovieListProvider::class) movies: List<Movie>) {
    MovieBoxTheme {
        MovieDetailContent(
            movie = movies[0],
            favoriteSaved = true,
        )
    }
}