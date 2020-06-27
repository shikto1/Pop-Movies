package com.movieplayer.android.ui.popular_movie

import dagger.Binds
import dagger.Module

@Module
abstract class PopularMovieViewModule {
    @Binds
    abstract fun providePopularMoviesView(activity: PopularMovieActivity): PopularMovieContract.View
}