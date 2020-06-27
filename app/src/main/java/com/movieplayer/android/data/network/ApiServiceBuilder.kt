package com.movieplayer.android.data.network
import retrofit2.Retrofit

class ApiServiceBuilder (private val retrofit: Retrofit) {

    fun <T> buildService(serviceType: Class<T>): T {
        return retrofit.create(serviceType)
    }
}