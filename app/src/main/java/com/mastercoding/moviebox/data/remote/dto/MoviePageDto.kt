package com.mastercoding.moviebox.data.remote.dto

import com.mastercoding.moviebox.domain.model.MoviePage
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MoviePageDto(
    val page: Int = 1,
    val results: List<MovieDto> = emptyList(),
    val total: Int = 0,
    val totalPages: Int = 1,
)

fun MoviePageDto.toDomain() = MoviePage(
    movies = results.map { it.toDomain() },
    page = page,
    totalPages = totalPages,
)
