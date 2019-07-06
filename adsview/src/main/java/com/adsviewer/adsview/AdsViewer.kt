package com.adsviewer.adsview

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import android.os.Build
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import android.graphics.Point
import android.util.Base64
import android.util.Log
import android.view.WindowManager
import com.adsviewer.adsview.models.*
import com.google.gson.Gson
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


class AdsViewer constructor(private var context: Context, private var isDebug: Boolean) {


    private lateinit var callRest: Disposable


    companion object {
        private var instance: AdsViewer? = null
        private val PRIVATE_MODE = 0
        private val PREF_NAME = "HUB_SDK_DETAILS"
        private val PREF_KEY = "ENC_KEY"
        private lateinit var deviceId: String
        private lateinit var packegeName: String
        private val pswdIterations = 10
        private val keySize = 128
        private val plainText = "sampleText"
        private val AESSalt = "Hub Ads Viewer"
        private val initializationVector = "8119745113154120"

        fun getInstance(): AdsViewer {
            if (instance != null) {
                return instance as AdsViewer
            } else
                throw IllegalAccessException("Hub Ads view not initialed")
        }

        fun initComponent(context: Context, isDebug: Boolean) {
            if (instance == null) {
                instance = AdsViewer(context, isDebug)
            }
        }

    }


    init {
        deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        packegeName = context.packageName
        if (getEncKey() == null) {
            registerToServer()
        }
    }

    private fun getAndroidVersion(): String {
        val release = Build.VERSION.RELEASE
        val sdkVersion = Build.VERSION.SDK_INT
        return "Android SDK: $sdkVersion ($release)"
    }

    private fun getEncKey(): String? {
        val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        return sharedPref.getString(PREF_KEY, null)
    }

    private fun saveEncKey(encKey: String) {
        val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        val edit = sharedPref.edit()
        edit.putString(PREF_KEY, encKey)
        edit.apply()
    }

    @SuppressLint("HardwareIds")
    private fun registerToServer() {

        val deviceName = Build.MANUFACTURER.toUpperCase()
        val deviceModel = Build.MODEL.toUpperCase()
        val androidVersion = getAndroidVersion()
        val request = InitDataFromServer(deviceId, deviceName, deviceModel, androidVersion, packegeName, isDebug)
        callRest = WebService.instance.initialDevice(request).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {}
            .doOnTerminate {}
            .subscribe({ result ->
                if (result.isSuccess && result.item != null) {
                    saveEncKey(result.item!!.encKey)
                }

            }, { error -> print(error.message) })
    }

    public fun getNewBanner(onNewAddReceived: onNewAddReceived) {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getRealSize(size)
        val newAddRequest = NewAdsRequest(deviceId, packegeName, size.x, size.y)
        callRest = WebService.instance.getNewAds(newAddRequest).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {}
            .doOnTerminate {}
            .subscribe({ result ->
                if (result.isSuccess && result.item != null) {
                    onNewAddReceived.onReceive(result.item!!)
                }

            }, { error -> print(error.message) })
    }

    public fun sendAddData(adsTimingModel: AdsTimingModel) {
        val toJson = Gson().toJson(adsTimingModel)
        var encrypt = String(Base64.encode(toJson.toByteArray(), Base64.DEFAULT))
        val adsStatics = AdsStatics(deviceId, packegeName, encrypt, isDebug)
        callRest = WebService.instance.sendAdsData(adsStatics).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {}
            .doOnTerminate {}
            .subscribe({ result ->
                if (result.isSuccess) {
                }

            }, { error -> print(error.message) })
    }

    public fun reformatUrl(url: String): String {
        return "$url?appPackage=$packegeName&deviceId=$deviceId";
    }


}

