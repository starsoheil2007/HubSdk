package com.adsviewer.adsview

import com.adsviewer.adsview.models.*
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import java.util.concurrent.TimeUnit

/**
 * Web Service Class to init Retrofit and call rest service
 *
 */
class WebService {

    companion object {
        const val BASE_URL = "http://sdk.diamondapp.ir"
        private const val WEB_SERVICE_URL = "$BASE_URL/api/v1/"

        val instance = WebService()

    }

    private lateinit var service: IRestService

    /**
     * init retrofit with logger in Gson Converter
     *
     */
    private fun provideRetrofitInterface(): Retrofit {

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        // set Timeouts
        httpClient.connectTimeout(20, TimeUnit.SECONDS)
        httpClient.readTimeout(20, TimeUnit.SECONDS)
        httpClient.writeTimeout(30, TimeUnit.SECONDS)
        // Show log in Debug
        if (BuildConfig.DEBUG) {
            httpClient.addInterceptor(logging)
        }

        // Config Gson
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
        gsonBuilder.registerTypeAdapter(BaseResponse::class.java, Deserializer<BaseResponse<Any>>())

        // Init Retrofit
        return Retrofit.Builder()
            .baseUrl(WEB_SERVICE_URL)
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
            .client(httpClient.build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    }

    /**
     * Init class in constructor called
     */
    init {
        val retrofit = provideRetrofitInterface()
        //Create retrofit service from IRestService
        service = retrofit.create(IRestService::class.java)
    }

    /**
     * send device info to server to get Encryption Key
     *
     * @param request instance of InitDataFromServer
     */
    fun initialDevice(@Body request: InitDataFromServer): Observable<BaseResponse<EncryptionKeyResponse>> {
        return service.initialDevice(request)
    }

    /**
     * send device info to server to get New Banner
     *
     * @param request instance of NewAdsRequest
     */
    fun getNewAds(@Body request: NewAdsRequest): Observable<BaseResponse<AdsModel>> {
        return service.getNewAds(request)
    }

    /**
     * send device info to server to get New Banner
     *
     * @param request instance of AdsStatics
     */
    fun sendAdsData(@Body request: AdsStatics): Observable<BaseResponse<Any>> {
        return service.sendAdsData(request)
    }

}