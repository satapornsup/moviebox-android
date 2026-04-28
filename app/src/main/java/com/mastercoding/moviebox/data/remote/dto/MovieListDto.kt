package com.mastercoding.moviebox.data.remote.dto

import com.mastercoding.moviebox.domain.model.Movie
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MovieListDto(
    val page: Int = 1,
    val results: List<MovieDto> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class MovieDto(
    val id: Int,
    val title: String,
    val overview: String = "",
    @param:Json(name = "poster_path") val posterPath: String? = null,
    @param:Json(name = "backdrop_path") val backdropPath: String? = null,
    @param:Json(name = "release_date") val releaseDate: String? = null,
    @param:Json(name = "vote_average") val voteAverage: Double = 0.0,
)

@JsonClass(generateAdapter = true)
data class FavoriteDto(
    val id: String? = null,
    val userId: String? = null,
    val movieId: Int,
    val title: String,
    val posterPath: String? = null,
    val voteAverage: Double = 0.0,
    val createdAt: String? = null,
)

// ── Mappers DTO → Domain ──

fun MovieDto.toDomain() = Movie(
    id = id,
    title = title,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
)

fun FavoriteDto.toDomain() = Movie(
    id = movieId,
    title = title,
    overview = "",
    posterPath = posterPath,
    backdropPath = null,
    releaseDate = null,
    voteAverage = voteAverage,
)
