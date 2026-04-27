package com.mastercoding.moviebox.presentation.nav

sealed class Route(val path: String) {
    data object Home : Route("home")
    data object Favorites : Route("favorites")
    data object MovieDetail : Route("movie_detail/{id}") {
        fun create(id: Int) = "movie_detail/$id"
    }
}
