package com.movieplayer.android.ui.movie_details

import com.movieplayer.android.R
import com.movieplayer.android.base.BasePresenter
import com.movieplayer.android.data.network.SSDisposableSingleObserver
import com.movieplayer.android.data.network.api_response.MovieDetailsResponse
import com.movieplayer.android.data.network.api_response.MovieTrailerResponse
import com.movieplayer.android.data.network.api_response.PopularMovieResponse
import com.movieplayer.android.data.network.api_service.TheMovieDbApiService
import com.movieplayer.android.ui.popular_movie.PopularMovieContract
import javax.inject.Inject

class MovieDetailsPresenter @Inject constructor(view: MovieDetailsContract.View) :
    BasePresenter<MovieDetailsContract.View>(view),
    MovieDetailsContract.Presenter {

    @Inject
    lateinit var theMovieDbApiService: TheMovieDbApiService


    override fun getMovieDetails(movieId: String, apiKey: String) {
        if (this::theMovieDbApiService.isInitialized) {
            mView?.onNetworkCallStarted(context.getString(R.string.please_wait))
            compositeDisposable?.add(
                theMovieDbApiService.getMovieDetails(movieId, apiKey)
                    .subscribeOn(appSchedulerProvider.io())
                    .unsubscribeOn(appSchedulerProvider.computation())
                    .observeOn(appSchedulerProvider.ui())
                    .subscribeWith(object :
                        SSDisposableSingleObserver<MovieDetailsResponse, MovieDetailsContract.View>(mView) {
                        override fun onRequestSuccess(response: MovieDetailsResponse) {
                            mView?.movieDetailsDidReceived(response)
                        }
                    })
            )
        }
    }

    override fun getMovieTrailers(movieId: String, apiKey: String) {
        if (this::theMovieDbApiService.isInitialized) {
            mView?.onNetworkCallStarted(context.getString(R.string.please_wait))
            compositeDisposable?.add(
                theMovieDbApiService.getMovieTrailers(movieId, apiKey)
                    .subscribeOn(appSchedulerProvider.io())
                    .unsubscribeOn(appSchedulerProvider.computation())
                    .observeOn(appSchedulerProvider.ui())
                    .subscribeWith(object :
                        SSDisposableSingleObserver<MovieTrailerResponse, MovieDetailsContract.View>(mView) {
                        override fun onRequestSuccess(response: MovieTrailerResponse) {
                            mView?.movieTrailersDidReceived(response)
                        }
                    })
            )
        }
    }
}