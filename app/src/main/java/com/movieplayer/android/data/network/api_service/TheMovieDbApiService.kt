package com.movieplayer.android.data.network.api_service

import com.movieplayer.android.data.network.APIs
import com.movieplayer.android.data.network.api_response.MovieDetailsResponse
import com.movieplayer.android.data.network.api_response.MovieTrailerResponse
import com.movieplayer.android.data.network.api_response.PopularMovieResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDbApiService {

    @GET(APIs.POPULAR_MOVIES)
    fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Single<PopularMovieResponse>

    @GET(APIs.MOVIE_DETAILS)
    fun getMovieDetails(
        @Path("movie_id") movieId: String,
        @Query("api_key") apiKey: String
    ): Single<MovieDetailsResponse>

    @GET(APIs.MOVIE_TRAILERS)
    fun getMovieTrailers(
        @Path("movie_id") movieId: String,
        @Query("api_key") apiKey: String
    ): Single<MovieTrailerResponse>

}