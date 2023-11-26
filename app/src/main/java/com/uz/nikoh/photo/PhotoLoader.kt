package com.uz.nikoh.photo

import android.widget.ImageView
import androidx.core.view.doOnLayout
import coil.load
import com.uz.base.imagekit.ImageKitUtils

fun ImageView.loadUrl(
    url: String,
    fullQuality: Boolean = false,
    fitViewSize: Boolean = true,
    fade: Boolean = true,
    blur: Int = 0
) {
    doOnLayout {
        val loadUrl = if (ImageKitUtils.isUrlImageKit(url)) {
            ImageKitUtils.buildUrl(url, measuredHeight, measuredWidth, fullQuality, blur = blur)
        } else url
        load(loadUrl) {
            if (fade) {
                crossfade(true)
            }
        }
    }
}