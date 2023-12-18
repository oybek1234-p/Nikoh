package com.uz.ui.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri

object LinkUtils {

    fun openUrl(activity: Activity, urlM: String) {
        var url = urlM
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://$url"
        }

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        activity.startActivity(browserIntent)
    }
}