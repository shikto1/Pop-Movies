package com.movieplayer.android.ui.popular_movie

import com.movieplayer.android.R
import com.movieplayer.android.base.BasePresenter
import com.movieplayer.android.data.network.SSDisposableSingleObserver
import com.movieplayer.android.data.network.api_response.PopularMovieResponse
import com.movieplayer.android.data.network.api_service.TheMovieDbApiService
import javax.inject.Inject

class PopularMoviePresenter @Inject constructor(view: PopularMovieContract.View) :
    BasePresenter<PopularMovieContract.View>(view),
    PopularMovieContract.Presenter {

    @Inject
    lateinit var theMovieDbApiService: TheMovieDbApiService

    override fun getPopularMovies(apiKey: String, page: Int) {
        if (this::theMovieDbApiService.isInitialized) {
            mView?.onNetworkCallStarted(context.getString(R.string.please_wait))
            compositeDisposable?.add(
                theMovieDbApiService.getPopularMovies(apiKey, page)
                    .subscribeOn(appSchedulerProvider.io())
                    .unsubscribeOn(appSchedulerProvider.computation())
                    .observeOn(appSchedulerProvider.ui())
                    .subscribeWith(object :
                        SSDisposableSingleObserver<PopularMovieResponse, PopularMovieContract.View>(mView) {
                        override fun onRequestSuccess(response: PopularMovieResponse) {
                            mView?.moviesDidReceived(response)
                        }
                    })
            )
        }
    }
}