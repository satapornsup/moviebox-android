package com.mastercoding.moviebox.data.repo

import com.mastercoding.moviebox.data.remote.MovieApi
import com.mastercoding.moviebox.data.remote.dto.FavoriteDto
import com.mastercoding.moviebox.data.remote.dto.toDomain
import com.mastercoding.moviebox.domain.model.Movie
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepository @Inject constructor(
    private val api: MovieApi
) {
    suspend fun list(): List<Movie> =
        api.favorites().map { it.toDomain() }

    suspend fun add(movie: Movie) {
        api.addFavorite(
            FavoriteDto(
                movieId = movie.id,
                title = movie.title,
                posterPath = movie.posterPath,
                voteAverage = movie.voteAverage,
            )
        )
    }

    suspend fun remove(movieId: Int) {
        api.removeFavorite(movieId)
    }
}