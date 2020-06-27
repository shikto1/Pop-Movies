package com.movieplayer.android.base

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import butterknife.ButterKnife
import butterknife.Unbinder
import com.movieplayer.android.data.prefs.SharedPrefManager
import com.movieplayer.android.utils.AppUtils

abstract class BaseActivity : AppCompatActivity() {

    protected val TAG: String  by lazy {
        this.javaClass.simpleName
    }

    private var progressDialog: ProgressDialog? = null
    private var unBinder: Unbinder? = null
    lateinit var mPrefs: SharedPrefManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPrefs = SharedPrefManager(getContext())
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

    protected fun getContext(): Context {
        return this
    }

    fun showProgressDialog(message: String) {
        if (null == progressDialog) {
            progressDialog = ProgressDialog(this)
            progressDialog!!.setCancelable(false)
        }
        progressDialog!!.setMessage(message)
        if (!progressDialog!!.isShowing) {
            progressDialog!!.show()
        }
    }

    fun hideProgressDialog() {
        progressDialog?.let {
            if (progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
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

    override fun onDestroy() {
        super.onDestroy()
        progressDialog?.let {
            if (progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
        }
        unBinder?.let {
            unBinder!!.unbind()
            unBinder = null
        }
    }
}