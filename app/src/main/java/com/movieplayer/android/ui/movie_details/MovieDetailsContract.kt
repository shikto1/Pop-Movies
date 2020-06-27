package com.movieplayer.android.ui.movie_details

import com.movieplayer.android.base.BaseContract
import com.movieplayer.android.data.network.api_response.MovieDetailsResponse
import com.movieplayer.android.data.network.api_response.MovieTrailerResponse

interface MovieDetailsContract {

    interface View : BaseContract.View {
        fun movieDetailsDidReceived(response: MovieDetailsResponse)
        fun movieTrailersDidReceived(response: MovieTrailerResponse)
    }

    interface Presenter : BaseContract.Presenter {
        fun getMovieDetails(
            movieId: String,
            apiKey: String
        )

        fun getMovieTrailers(
            movieId: String,
            apiKey: String
        )
    }
}