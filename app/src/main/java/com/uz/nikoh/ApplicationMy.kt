package com.uz.nikoh

import android.app.Application
import android.content.Context
import com.uz.base.imagekit.ImageKitUtils

lateinit var appContext: Context

class ApplicationMy : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this
        ImageKitUtils.initImageKit(this)
    }
}