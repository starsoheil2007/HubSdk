package com.adsviewer.adsview.models

data class InitDataFromServer(
    var deviceId: String,
    var deviceName: String?,
    var deviceModel: String?,
    var androidVersion: String?,
    var appPackage: String,
    var isDebug: Boolean
)