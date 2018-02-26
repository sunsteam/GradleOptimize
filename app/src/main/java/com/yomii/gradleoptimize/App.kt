package com.yomii.gradleoptimize

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.support.multidex.MultiDex
import android.util.Log
import com.letv.sarrsdesktop.blockcanaryex.jrt.BlockCanaryEx
import com.letv.sarrsdesktop.blockcanaryex.jrt.Config
import com.squareup.leakcanary.LeakCanary
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.tinker.loader.app.ApplicationLike

/**
 * Application
 *
 * Created by Yomii on 2018/2/22.
 */

class App(application: Application, tinkerFlags: Int, tinkerLoadVerifyFlag: Boolean, applicationStartElapsedTime: Long, applicationStartMillisTime: Long, tinkerResultIntent: Intent)
    : ApplicationLike(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent) {

    private val TAG = "App"

    override fun onCreate() {
        super.onCreate()
        val currentProcessName = getCurrentProcessName()
        Log.i(TAG, "onCreate currentProcessName=" + currentProcessName)
        val application = application
        if (application.packageName == currentProcessName) {
            BlockCanaryEx.install(Config(application))
            LeakCanary.install(application)

            val context: Context = application
            Bugly.setIsDevelopmentDevice(context, BuildConfig.DEBUG)
            val strategy = CrashReport.UserStrategy(context)
            strategy.isUploadProcess = !BuildConfig.DEBUG
            Bugly.init(context, "03e72b0366", BuildConfig.DEBUG, strategy)
        }
    }

    override fun onBaseContextAttached(base: Context?) {
        super.onBaseContextAttached(base)
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base)
        // 安装tinker
        Beta.installTinker(this)
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
