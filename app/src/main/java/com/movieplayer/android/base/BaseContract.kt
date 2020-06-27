package com.movieplayer.android.base

import android.content.Context

interface BaseContract {

    interface View {

        fun onNetworkUnavailable()

        fun onNetworkCallStarted(loadingMessage: String)

        fun onNetworkCallEnded()

        fun onServerError()

        fun onTimeOutError()

        fun onUserUnauthorized()

        fun onSystemUpgrading()

        fun onUserDidTooManyAttempts(errorMsg: String)

        fun on404()

        fun onMethodNotSupported(errorMsg: String)

        fun getContext(): Context

    }

    interface Presenter {

        fun detachView()

        fun clearDisposable()

    }
}