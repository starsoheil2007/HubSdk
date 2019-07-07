package com.adsviewer.adsview.helper

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import java.util.concurrent.ExecutionException

object ImageLoaderHelper {

    private val GLIDE_MEMORY_CACHE_SIZE = 0 //0 MB
    private val GLIDE_DISK_CACHE_SIZE = 20 * 1000 //20 MB


    fun displayImage(
        context: Context,
        imageView: ImageView,
        imageUrl: String,
        defaultImageResourceId: Int?,
        listener: RequestListener<Drawable>?
    ) {

        var builder = Glide.with(context)
            .load(imageUrl)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .listener(listener)

        if (defaultImageResourceId != null) {
            builder = builder.error(defaultImageResourceId)
        }
        builder.into(imageView)
    }


    fun displayGifImage(
        context: Context,
        imageView: ImageView,
        imageResource: Int,
        listener: RequestListener<GifDrawable>?
    ) {
        val builder = Glide.with(context)
            .asGif()
            .load(imageResource)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .listener(listener)

        builder.into(imageView)
    }


    fun displayGifImageUrl(
        context: Context,
        imageView: ImageView,
        imageUrl: String,
        listener: RequestListener<GifDrawable>?
    ) {
        val builder = Glide.with(context)
            .asGif()
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .listener(listener)

        builder.into(imageView)
    }


    fun downloadImage(context: Context, imageUrl: String, listener: RequestListener<Drawable>?): Drawable? {
        return Glide.with(context)
            .load(imageUrl)
            .listener(listener)
            .submit()
            .get()
    }

    fun loadImageSync(context: Context, imageUrl: String): Drawable? {
        try {
            return Glide.with(context)
                .load(imageUrl)
                .submit()
                .get()
        } catch (e: ExecutionException) {
            e.printStackTrace()
            return null
        }

    }


}