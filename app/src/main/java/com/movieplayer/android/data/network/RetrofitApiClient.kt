package com.movieplayer.android.data.network

import android.content.Context
import android.util.Log
import com.movieplayer.android.BuildConfig
import com.movieplayer.android.data.prefs.PrefKeys
import com.movieplayer.android.data.prefs.SharedPrefManager
import com.movieplayer.android.utils.NetworkUtils
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


object RetrofitApiClient {

    private const val HEADER_CACHE_CONTROL = "Cache-Control"
    private const val HEADER_PRAGMA = "Pragma"
    private const val CACHE_SIZE = 10 * 1024 * 1024 // 10 MB
    private const val MAX_AGE = 10 // in Minutes
    private const val MAX_STALE = 1 // in Days


    fun getRetrofit(
        context: Context,
        networkUtils: NetworkUtils,
        preferenceManager: SharedPrefManager
    ): Retrofit {


        /** Cache  Object*/
        val httpCacheDirectory = File(context.applicationContext.cacheDir, "http-cache")
        val cache = Cache(httpCacheDirectory, CACHE_SIZE.toLong())

        /**
         * This interceptor will be called ONLY if the network is available
         */

        val networkInterceptor = Interceptor { chain ->
            if (BuildConfig.DEBUG) {
                Log.e("NETWORK INTERCEPTOR", " >>>>>>>>>>>> START")
            }

            val response = chain.proceed(chain.request())

            val cacheControl = CacheControl.Builder()
                .maxAge(MAX_AGE, TimeUnit.MINUTES)
                .build()

            if (BuildConfig.DEBUG) {
                Log.e("NETWORK INTERCEPTOR", " >>>>>>>>>>>> END")
            }

            val responseBuilder = response.newBuilder()
            if (APIs.CACHING_URLs.isNotEmpty() && APIs.CACHING_URLs.contains(chain.request().url().toString())) {
                responseBuilder.removeHeader(HEADER_PRAGMA)
                responseBuilder.removeHeader(HEADER_CACHE_CONTROL)
                responseBuilder.header(HEADER_CACHE_CONTROL, cacheControl.toString())
            }
            responseBuilder.build()
        }

        /**
         * This interceptor will be called both if the network is available and if the network is not available
         */
        val offlineInterceptor = Interceptor { chain ->
            if (BuildConfig.DEBUG) {
                Log.e("OFFLINE INTERCEPTOR", ">>>>> START.")
            }

            if (!networkUtils.isConnectedToNetwork(context)) {
                throw NoConnectivityException()
            } else {


                val request = chain.request()
                val requestBuilder = request.newBuilder()
                requestBuilder.addHeader("Accept", Headers.ACCEPT)
                requestBuilder.addHeader("content-type", Headers.MULTIPART_FORM_DATA)

                /** AUTHORIZATION TOKEN  */
                if (!APIs.NO_AUTH_URLs.contains(chain.request().url().toString())) {
                    requestBuilder.addHeader(
                        "Authorization",
                        "${Headers.BEARER}${preferenceManager.getString(PrefKeys.ACCESS_TOKEN)}"
                    )
                }

                /** CACHED CHECKED >>>> */
                if (APIs.CACHING_URLs.isNotEmpty() && APIs.CACHING_URLs.contains(chain.request().url().toString())) {
                    val cacheControl = CacheControl.Builder()
                        .maxStale(MAX_STALE, TimeUnit.DAYS)
                        .build()
                    requestBuilder.removeHeader(HEADER_PRAGMA)
                    requestBuilder.removeHeader(HEADER_CACHE_CONTROL)
                    requestBuilder.cacheControl(cacheControl)
                }

                if (BuildConfig.DEBUG) {
                    Log.e("OFFLINE INTERCEPTOR", ">>>>> END.")
                }
                chain.proceed(requestBuilder.build())
            }
        }


        /** Logging Interceptor */
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY


        /** http Client Builder, configure it with cache, network cache interceptor and logging interceptor*/
        val httpClientBuilder = OkHttpClient.Builder()
            .cache(cache)
            .addNetworkInterceptor(networkInterceptor)
            .addInterceptor(offlineInterceptor)

        /** Adding Logging Interceptor only for DEBUG mode >>>>>>>>>>*/
        if (BuildConfig.DEBUG) {
            httpClientBuilder.addInterceptor(loggingInterceptor)
        }

        return Retrofit.Builder()
            .baseUrl(APIs.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClientBuilder.build())
            .build()
    }
}