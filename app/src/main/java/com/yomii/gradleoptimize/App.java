package com.yomii.gradleoptimize;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Application
 *
 * Created by Yomii on 2018/2/22.
 */

public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
