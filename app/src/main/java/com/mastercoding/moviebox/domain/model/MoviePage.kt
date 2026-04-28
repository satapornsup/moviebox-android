package com.mastercoding.moviebox.domain.model

data class MoviePage(
    val movies: List<Movie>,
    val page: Int,
    val totalPages: Int,
)
