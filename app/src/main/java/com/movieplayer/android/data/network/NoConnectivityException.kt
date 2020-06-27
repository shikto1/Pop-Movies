package com.movieplayer.android.data.network
import java.io.IOException

class NoConnectivityException : IOException() {
    override val message: String?
        get() = "No Internet Connection !"

}