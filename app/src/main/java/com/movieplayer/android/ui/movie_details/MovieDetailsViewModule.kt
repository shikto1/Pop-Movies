package com.movieplayer.android.ui.movie_details

import dagger.Binds
import dagger.Module

@Module
abstract class MovieDetailsViewModule {
    @Binds
    abstract fun provideMovieDetailsView(activity: MovieDetailsActivity): MovieDetailsContract.View
}