package com.adsviewer.test

import android.app.Application
import android.os.Handler
import com.adsviewer.adsview.AdsViewer
import com.adsviewer.adsview.models.AdsTimingModel
import java.util.*

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AdsViewer.initComponent(this, isDebug = false)

        var date = Date()
        Handler().postDelayed(Runnable {
            var date = Date()
            var endDate = System.currentTimeMillis()/1000
        }, 5000)
    }
}