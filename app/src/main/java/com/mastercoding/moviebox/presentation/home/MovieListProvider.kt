package com.mastercoding.moviebox.presentation.home

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.mastercoding.moviebox.domain.model.Movie
private val movieList = listOf(
    Movie(
        id = 1,
        title = "The Shawshank Redemption",
        posterPath = null,
        releaseDate = "1994-09-23",
        voteAverage = 2.0,
        backdropPath = null,
        overview = "Two imprisoned men bond over years, finding hope and redemption through acts of decency."
    ),
    Movie(
        id = 2,
        title = "Inception",
        posterPath = null,
        releaseDate = "2010-07-16",
        voteAverage = 3.4,
        backdropPath = null,
        overview = "A skilled thief enters dreams to steal secrets but is given a chance to erase his past."
    ),
    Movie(
        id = 3,
        title = "Interstellar",
        posterPath = null,
        releaseDate = "2014-11-07",
        voteAverage = 4.6,
        backdropPath = null,
        overview = "A team travels through a wormhole in search of a new home for humanity."
    ),
    Movie(
        id = 4,
        title = "The Dark Knight",
        posterPath = null,
        releaseDate = "2008-07-18",
        voteAverage = 4.8,
        backdropPath = null,
        overview = "Batman faces the Joker, a criminal mastermind who pushes Gotham into chaos."
    ),
    Movie(
        id = 5,
        title = "Fight Club",
        posterPath = null,
        releaseDate = "1999-10-15",
        voteAverage = 4.2,
        backdropPath = null,
        overview = "An insomniac forms an underground fight club that spirals into something far bigger."
    ),
    Movie(
        id = 6,
        title = "Forrest Gump",
        posterPath = null,
        releaseDate = "1994-07-06",
        voteAverage = 4.1,
        backdropPath = null,
        overview = "A simple man unknowingly influences major historical events through his extraordinary life."
    ),
    Movie(
        id = 7,
        title = "The Matrix",
        posterPath = null,
        releaseDate = "1999-03-31",
        voteAverage = 4.5,
        backdropPath = null,
        overview = "A hacker discovers reality is a simulation and joins a rebellion against machines."
    ),
    Movie(
        id = 8,
        title = "Gladiator",
        posterPath = null,
        releaseDate = "2000-05-05",
        voteAverage = 4.0,
        backdropPath = null,
        overview = "A betrayed Roman general fights as a gladiator to seek revenge and restore honor."
    )
)

class MovieListProvider : PreviewParameterProvider<List<Movie>> {
    override val values = sequenceOf(
        movieList
    )
}