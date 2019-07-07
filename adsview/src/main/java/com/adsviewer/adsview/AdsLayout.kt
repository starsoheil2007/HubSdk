package com.adsviewer.adsview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.StyleableRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.adsviewer.adsview.helper.ImageLoaderHelper
import com.adsviewer.adsview.models.AdsModel
import com.adsviewer.adsview.models.AdsTimingModel
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target


class AdsLayout : RelativeLayout {

    @StyleableRes
    var index0 = 0
    @StyleableRes
    var index1 = 1

    lateinit var layout: LinearLayout
    var startDate: Long? = null
    var endDate: Long? = null
    var thisAdsModel: AdsModel? = null

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initComponent(context, attrs)

    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initComponent(context, attrs)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun initComponent(context: Context, attrs: AttributeSet) {
        inflate(context, R.layout.adsviewer_layout, this)
        animate()
            .translationY(-100f)
            .alpha(0.0f)
            .setDuration(10)
            .setListener(object : AnimatorListenerAdapter() {

            })

        val sets = intArrayOf(R.attr.minTime, R.attr.type)
        val typedArray = context.obtainStyledAttributes(attrs, sets)
        typedArray.recycle()

        var imageView = findViewById<AppCompatImageView>(R.id.image)
        var webView = findViewById<WebView>(R.id.webView)
        var closeBtn = findViewById<AppCompatImageView>(R.id.closeBtn)
        layout = findViewById<LinearLayout>(R.id.layout)

        imageView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        bringToFront()
                        resizeHieght()
                        sendAdsDataToServer(true)
                    }
                    MotionEvent.ACTION_UP -> {
                    }
                }
                return false
            }
        })

        closeBtn.setOnClickListener {
            closeTheView(false)
        }

        AdsViewer.getInstance().getNewBanner(object : onNewAddReceived {
            override fun onReceive(adsModel: AdsModel) {
                thisAdsModel = adsModel
                if (adsModel.type == 1) {
                    imageView.visibility = View.VISIBLE
                    ImageLoaderHelper.displayImage(
                        context,
                        imageView,
                        WebService.BASE_URL + adsModel.url,
                        null,
                        object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                showTheView()
                                return false
                            }
                        })
                } else if (adsModel.type == 2) {
                    imageView.visibility = View.VISIBLE
                    ImageLoaderHelper.displayGifImageUrl(
                        context,
                        imageView,
                        WebService.BASE_URL + adsModel.url,
                        object : RequestListener<GifDrawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<GifDrawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }

                            override fun onResourceReady(
                                resource: GifDrawable?,
                                model: Any?,
                                target: Target<GifDrawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                showTheView()
                                return false
                            }
                        })
                } else if (adsModel.type == 4) {
                    webView.visibility = View.VISIBLE
                    webView.clearCache(true);
                    webView.clearHistory();
                    webView.settings.javaScriptEnabled = true;
                    webView.settings.javaScriptCanOpenWindowsAutomatically = true;
                    webView.loadUrl(AdsViewer.getInstance().reformatUrl(adsModel.url))

                }
            }
        })

    }

    private fun getHeightScreen(): Int {
        val displayMetrics = DisplayMetrics()
        (context as AppCompatActivity).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    private fun resizeHieght() {
        val anim = ValueAnimator.ofInt(measuredHeight, getHeightScreen())
        anim.addUpdateListener { valueAnimator ->
            val `val` = valueAnimator.animatedValue as Int
            val changeParams = Runnable {
                val layoutParams = layoutParams
                layoutParams.height = `val`
                setLayoutParams(layoutParams)
            }
            post(changeParams)
        }
        anim.interpolator = AccelerateDecelerateInterpolator()
        anim.duration = 1000
        anim.start()
    }

    private fun showTheView() {
        animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(1000)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    startDate = System.currentTimeMillis() / 1000
                }
            })
    }

    private fun closeTheView(isClicked: Boolean) {
        animate()
            .translationY(1000f)
            .alpha(0.0f)
            .setDuration(1000)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    super.onAnimationStart(animation)
                    endDate = System.currentTimeMillis() / 1000
                    sendAdsDataToServer(isClicked)
                }
            })
    }


    private fun sendAdsDataToServer(isClicked: Boolean) {
        if (startDate != null && thisAdsModel != null) {
            if (endDate == null)
                endDate = System.currentTimeMillis() / 1000
            AdsViewer.getInstance()
                .sendAddData(AdsTimingModel(thisAdsModel!!.id, startDate.toString(), endDate.toString(), isClicked))
        }

    }
}