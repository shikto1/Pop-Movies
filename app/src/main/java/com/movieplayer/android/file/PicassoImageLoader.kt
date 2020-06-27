package com.movieplayer.android.file

import android.content.Context
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class PicassoImageLoader private constructor() {

    companion object {
        val sharedInstance by lazy { PicassoImageLoader() }
    }

    fun loadImage(context: Context, imageUrl: String, imageView: ImageView, placeHolderImageId: Int) {
        Picasso.with(context)
            .load(imageUrl)
            .networkPolicy(NetworkPolicy.OFFLINE)
            .into(imageView, object : Callback {
                override fun onSuccess() {}

                override fun onError() {
                    Picasso.with(context)
                        .load(imageUrl)
                        .placeholder(placeHolderImageId)
                        .error(placeHolderImageId)
                        .into(imageView)
                }
            })
    }

    fun loadImage(context: Context, imageUrl: String, imageView: ImageView) {
        Picasso.with(context)
            .load(imageUrl)
            .networkPolicy(NetworkPolicy.OFFLINE)
            .into(imageView, object : Callback {
                override fun onSuccess() {}

                override fun onError() {
                    Picasso.with(context)
                        .load(imageUrl)
                        .into(imageView)
                }
            })
    }

    fun loadImage(context: Context, imageUrl: String?) {
        if (imageUrl != null && !imageUrl.isEmpty())
            Picasso.with(context).load(imageUrl)
    }
}
