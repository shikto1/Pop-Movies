package com.movieplayer.android.data.network

import com.movieplayer.android.base.BaseContract
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.observers.DisposableSingleObserver
import org.json.JSONObject
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*
import javax.net.ssl.SSLHandshakeException

/**
 * Created on 09/07/2019.
 */

abstract class SSDisposableSingleObserver<R, out V : BaseContract.View>(private val mView: V?) :
    DisposableSingleObserver<R>() {

    abstract fun onRequestSuccess(response: R)
    protected open fun onFormDataNotValid(errorMap: LinkedHashMap<String, String>) {}
    protected open fun onExpectationFailed(errorMsg: String) {}

    override fun onSuccess(response: R) {
        mView?.onNetworkCallEnded()
        onRequestSuccess(response)
    }

    override fun onError(throwable: Throwable) {
        mView?.onNetworkCallEnded()
        when (throwable) {
            is NoConnectivityException -> mView?.onNetworkUnavailable()
            is HttpException -> {
                val errorCode = throwable.code()
                val res = throwable.response()
                when (errorCode) {
                    401 -> mView?.onUserUnauthorized()
                    404 -> mView?.on404()           // 404 = NOT FOUND
                    405 -> {
                        try {

                        } catch (exception: Exception) {
                            mView?.onMethodNotSupported("Method Not Supported")
                        }
                    }
                    417 -> { // 417 = Expectation Failed
                        try {
                            val jsnStr = res.errorBody()?.string()
                            if (jsnStr != null) {
                                val rootObj = JSONObject(jsnStr)
                                val errorMsg = rootObj.getString("message")
                                onExpectationFailed(errorMsg)
                            }
                        } catch (exception: Exception) {
                            onExpectationFailed("Expectation Failed")
                        }
                    }
                    422 -> {          // 422 = FORM DATA NOT VALID
                        try {
                            val jsnStr = res.errorBody()?.string()
                            if (jsnStr != null) {
                                val rootObj = JSONObject(jsnStr)
                                val errors = rootObj.getJSONObject("body")
                                val errorMap = Gson().fromJson<LinkedHashMap<String, String>>(
                                    errors.toString(),
                                    object : TypeToken<LinkedHashMap<String, String>>() {}.type
                                )
                                onFormDataNotValid(errorMap)
                            }
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                        }
                    }
                    500 -> mView?.onServerError()
                }
            }
            is SocketTimeoutException, is UnknownHostException -> mView?.onServerError()
            is SSLHandshakeException, is ConnectException -> mView?.onTimeOutError()
        }
    }
}

/**
 *  .........................................................................
 *  SocketTimeoutException  -> Might be internet connection is okay but could not connect with server. It is basically SERVER ISSUE.
 *  SSLHandshakeException   -> Might be the internet connection is slow or your app cannot connect to the server. It is basically low CONNECTIVITY ISSUE.
 *  Connect Exception       -> This is SERVER ISSUE
 */