package com.adsviewer.adsview


import com.adsviewer.adsview.models.*
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface IRestService {

    /**
     * send device info to server to get Encryption Key
     *
     * @param request instance of InitDataFromServer
     */
    @POST("sdk/initial_device/")
    fun initialDevice(@Body request: InitDataFromServer): Observable<BaseResponse<EncryptionKeyResponse>>

    /**
     * send device info to server to get Encryption Key
     *
     * @param request instance of InitDataFromServer
     */
    @POST("sdk/new_ads/")
    fun getNewAds(@Body request: NewAdsRequest): Observable<BaseResponse<AdsModel>>

    /**
     * send device info to server to get Encryption Key
     *
     * @param adsStatics instance of InitDataFromServer
     */
    @POST("sdk/ads_data/")
    fun sendAdsData(@Body adsStatics: AdsStatics): Observable<BaseResponse<Any>>


}