package com.yomii.gradleoptimize

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.support.multidex.MultiDex
import android.util.Log
import com.squareup.leakcanary.LeakCanary
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.tinker.entry.DefaultApplicationLike
import com.tencent.tinker.lib.tinker.TinkerInstaller



/**
 * Application
 *
 * Created by Yomii on 2018/2/22.
 */

class App(application: Application, tinkerFlags: Int, tinkerLoadVerifyFlag: Boolean, applicationStartElapsedTime: Long, applicationStartMillisTime: Long, tinkerResultIntent: Intent)
    : DefaultApplicationLike(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent) {

    private val TAG = "App"

    override fun onCreate() {
        super.onCreate()
        val currentProcessName = getCurrentProcessName()
        Log.i(TAG, "onCreate currentProcessName=" + currentProcessName)
        val application = application
        if (application.packageName == currentProcessName) {
            LeakCanary.install(application)

            val context: Context = application
            CrashReport.setIsDevelopmentDevice(context, BuildConfig.DEBUG)
            val strategy = CrashReport.UserStrategy(context)
            strategy.isUploadProcess = !BuildConfig.DEBUG
            CrashReport.initCrashReport(context, "03e72b0366", BuildConfig.DEBUG, strategy)
        }
    }

    override fun onBaseContextAttached(base: Context?) {
        super.onBaseContextAttached(base)
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base)
        // 安装tinker
        TinkerInstaller.install(this)
    }

    private fun getCurrentProcessName(): String {
        val pid = android.os.Process.myPid()
        val manager = application.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (processInfo in manager.runningAppProcesses) {
            if (processInfo.pid == pid) {
                return processInfo.processName
            }
        }
        return ""
    }
}
