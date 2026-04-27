package com.mastercoding.moviebox.data.remote

import com.mastercoding.moviebox.data.remote.dto.FavoriteDto
import com.mastercoding.moviebox.data.remote.dto.MovieDto
import com.mastercoding.moviebox.data.remote.dto.MovieListDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {
    @GET("movies/popular")
    suspend fun popular(@Query("page") page: Int = 1): MovieListDto

    @GET("movies/search")
    suspend fun search(@Query("q") query: String): MovieListDto

    @GET("movies/{id}")
    suspend fun detail(@Path("id") id: Int): MovieDto

    @GET("favorites")
    suspend fun favorites(): List<FavoriteDto>

    @POST("favorites")
    suspend fun addFavorite(@Body body: FavoriteDto): FavoriteDto

    @DELETE("favorites/{movieId}")
    suspend fun removeFavorite(@Path("movieId") movieId: Int)
}