package com.mastercoding.moviebox.data.repo

import com.mastercoding.moviebox.data.remote.MovieApi
import com.mastercoding.moviebox.data.remote.dto.toDomain
import com.mastercoding.moviebox.domain.model.Movie
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val api: MovieApi
) {

    suspend fun popular(): List<Movie> =
        api.popular().results.map { it.toDomain() }

    suspend fun search(query: String): List<Movie> =
        api.search(query).results.map { it.toDomain() }

    suspend fun detail(id: Int): Movie =
        api.detail(id).toDomain()
}


