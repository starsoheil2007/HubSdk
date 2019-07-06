package com.adsviewer.adsview

import com.adsviewer.adsview.models.AdsModel

interface onNewAddReceived {
    fun onReceive(adsModel: AdsModel)
}