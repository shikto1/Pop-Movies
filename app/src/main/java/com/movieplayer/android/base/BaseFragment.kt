package com.movieplayer.android.base

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import butterknife.Unbinder
import com.movieplayer.android.R
import com.movieplayer.android.data.prefs.SharedPrefManager
import com.movieplayer.android.utils.AlertService
import com.movieplayer.android.utils.AppLogger
import com.movieplayer.android.utils.Navigator
import com.movieplayer.android.utils.NetworkUtils
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

abstract class BaseFragment<P : BaseContract.Presenter> : Fragment(), BaseContract.View {

    protected val TAG: String  by lazy {
        this.javaClass.simpleName
    }

    private var progressDialog: ProgressDialog? = null
    private var unBinder: Unbinder? = null

    @Inject
    lateinit var mPresenter: P

    @Inject
    lateinit var mAlertService: AlertService

    @Inject
    lateinit var mNetworkUtils: NetworkUtils

    @Inject
    lateinit var mAppLogger: AppLogger

    @Inject
    lateinit var mPrefManager: SharedPrefManager

    protected lateinit var rootView: View


    @Nullable
    override fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        rootView = getFragmentView(inflater, container, savedInstanceState)
        unBinder = ButterKnife.bind(this, rootView)
        onViewReady(arguments)
        return rootView
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }


    protected abstract fun getFragmentView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saveInstanceState: Bundle?
    ): View

    protected abstract fun onViewReady(getArguments: Bundle?)

    override fun onNetworkUnavailable() {
        mAlertService.showAlert(
            context,
            getString(R.string.no_internet_connection),
            getString(R.string.no_internet_msg)
        )
    }

    protected fun showProgressDialog(message: String) {
        if (null == progressDialog) {
            progressDialog = ProgressDialog(context)
            progressDialog!!.setCancelable(false)
        }
        progressDialog!!.setMessage(message)
        if (!progressDialog!!.isShowing) {
            progressDialog!!.show()
        }
    }

    protected fun hideProgressDialog() {
        progressDialog?.let { dialog ->
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
    }

    override fun onNetworkCallStarted(loadingMessage: String) {
        showProgressDialog(loadingMessage)
    }

    override fun onNetworkCallEnded() {
        hideProgressDialog()
    }

    override fun onServerError() {
        mAlertService.showToast(getString(R.string.something_wrong))
    }

    override fun onTimeOutError() {
        mAlertService.showToast(getString(R.string.something_wrong))
    }

    override fun onUserUnauthorized() {
        // >>>>
    }

    override fun onSystemUpgrading() {
        //mAlertService.showToast("System is upgrading...")
    }


    override fun onUserDidTooManyAttempts(errorMsg: String) {

    }

    override fun on404() {
    }

    override fun onMethodNotSupported(errorMsg: String) {

    }

    override fun getContext(): Context {
        return activity!!
    }


    override fun onStop() {
        super.onStop()
        mPresenter.clearDisposable()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
        progressDialog?.let { dialog ->
            if (dialog.isShowing) dialog.dismiss()
        }
        if (unBinder != null) {
            unBinder?.unbind()
            unBinder = null
        }
    }

}