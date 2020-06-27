package com.movieplayer.android.base

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import butterknife.Unbinder
import com.movieplayer.android.R
import com.movieplayer.android.data.prefs.SharedPrefManager
import com.movieplayer.android.utils.*
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

abstract class MvpBaseActivity<P : BaseContract.Presenter> : AppCompatActivity(), BaseContract.View,
    HasSupportFragmentInjector {

    protected val TAG: String  by lazy {
        this.javaClass.simpleName
    }

    private var progressDialog: ProgressDialog? = null
    private var unBinder: Unbinder? = null

    @Inject
    lateinit var mFragmentInjector: DispatchingAndroidInjector<Fragment>

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(getContentView())
        unBinder = ButterKnife.bind(this)
        supportActionBar?.let { actionBar ->
            val launcherActivity = AppUtils.sharedInstance.getAppLauncherActivity(getContext())
            val currentActivity = this.javaClass.canonicalName
            if (currentActivity != launcherActivity) {
                actionBar.apply {
                    setDisplayHomeAsUpEnabled(true)
                    setDisplayShowHomeEnabled(true)
                }
            }
        }
        onViewReady(savedInstanceState, intent)
    }

    abstract fun getContentView(): Int
    abstract fun onViewReady(savedInstanceState: Bundle?, intent: Intent)

    protected fun showProgressDialog(message: String) {
        if (null == progressDialog) {
            progressDialog = ProgressDialog(this, R.style.MyAlertDialogStyle)
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            if (item.itemId == android.R.id.home) {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun getContext(): Context {
        return this
    }

    override fun onNetworkUnavailable() {
        mAlertService.showAlert(
            getContext(),
            getString(R.string.no_internet_connection),
            getString(R.string.no_internet_msg)
        )
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
        // On UnAuthorized >>>
    }

    override fun onSystemUpgrading() {
        //mAlertService.showToast("System is upgrading...")
    }


    override fun onUserDidTooManyAttempts(errorMsg: String) {

    }

    override fun on404() {
        showToast("Not Found")
    }

    override fun onMethodNotSupported(errorMsg: String) {

    }

    protected fun currentScreenOrientation(): Int {
        return getContext().resources.configuration.orientation
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return mFragmentInjector
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