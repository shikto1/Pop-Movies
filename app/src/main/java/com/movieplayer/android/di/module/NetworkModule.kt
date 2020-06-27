package com.movieplayer.android.di.module

import android.content.Context
import com.movieplayer.android.data.network.ApiServiceBuilder
import com.movieplayer.android.data.network.RetrofitApiClient
import com.movieplayer.android.data.network.api_service.TheMovieDbApiService
import com.movieplayer.android.data.prefs.SharedPrefManager
import com.movieplayer.android.utils.NetworkUtils
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofitApiClient(
        context: Context,
        networkUtils: NetworkUtils,
        preferenceManager: SharedPrefManager
    ): Retrofit {
        return RetrofitApiClient.getRetrofit(context, networkUtils, preferenceManager)
    }

    @Provides
    @Singleton
    fun provideApiServiceBuilder(retrofit: Retrofit): ApiServiceBuilder {
        return ApiServiceBuilder(retrofit)
    }

    //////////////////////////////////////////////////////////////////

    @Provides
    @Singleton
    fun provideUserApiService(apiServiceBuilder: ApiServiceBuilder): TheMovieDbApiService {
        return apiServiceBuilder.buildService(TheMovieDbApiService::class.java)
    }

}