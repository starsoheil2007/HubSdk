package com.adsviewer.test

import android.app.Application
import com.adsviewer.adsview.AdsViewer

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AdsViewer.initComponent(this, isDebug = false)
    }
}