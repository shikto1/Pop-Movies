package com.movieplayer.android.ui.popular_movie

import com.movieplayer.android.base.BaseContract
import com.movieplayer.android.data.network.api_response.PopularMovieResponse

interface PopularMovieContract {

    interface View : BaseContract.View {

        fun moviesDidReceived(response: PopularMovieResponse)
    }

    interface Presenter : BaseContract.Presenter {
        fun getPopularMovies(
            apiKey: String,
            page: Int
        )
    }
}