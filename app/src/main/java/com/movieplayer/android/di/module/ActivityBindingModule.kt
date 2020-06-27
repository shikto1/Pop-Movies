package com.movieplayer.android.di.module

import com.movieplayer.android.di.scope.ActivityScope
import com.movieplayer.android.ui.movie_details.MovieDetailsActivity
import com.movieplayer.android.ui.movie_details.MovieDetailsViewModule
import com.movieplayer.android.ui.popular_movie.PopularMovieActivity
import com.movieplayer.android.ui.popular_movie.PopularMovieViewModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(includes = [FragmentBindingModule::class])
abstract class ActivityBindingModule {

    /** popular movie */
    @ActivityScope
    @ContributesAndroidInjector(modules = [PopularMovieViewModule::class])
    abstract fun bindPopularMoviesActivity(): PopularMovieActivity


    /** movie details */
    @ActivityScope
    @ContributesAndroidInjector(modules = [MovieDetailsViewModule::class])
    abstract fun bindMovieDetailsActivity(): MovieDetailsActivity


}