package com.mastercoding.moviebox.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import kotlin.Int

@Composable
fun MovieRow(movie: Movie, onClick: (Int) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(movie.id) }
            .padding(vertical = 4.dp),
    ) {
        AsyncImage(
            model = movie.posterPath?.let { "https://image.tmdb.org/t/p/w185$it" },
            contentDescription = movie.title,
            placeholder = painterResource(R.drawable.outline_image_24),
            error = painterResource(R.drawable.outline_broken_image_24),
            fallback = painterResource(R.drawable.outline_image_24),
            modifier = Modifier.size(60.dp, 90.dp),
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.fillMaxWidth()) {
            Text(
                movie.title,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge
            )
            if (movie.releaseDate != null) {
                Text(movie.releaseDate, style = MaterialTheme.typography.bodySmall)
            }
            Text("★ %.1f".format(movie.voteAverage), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MovieRowPreview(@PreviewParameter(MovieListProvider::class) movies: List<Movie>) {
    MovieBoxTheme { MovieRow(movies[0], onClick = {}) }
}