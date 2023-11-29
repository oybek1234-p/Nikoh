package com.uz.ui

import android.app.Activity
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.uz.nikoh.appContext

object AppTheme {

    val isNightMode: Boolean get() = BaseConfig.isDarkTheme

    fun setTheme(activity: MainActivity) {
        AppCompatDelegate.setDefaultNightMode(
            if (isNightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        activity.themeChanged()
    }

    fun toggleTheme(activity: Activity) {
        BaseConfig.apply {
            isDarkTheme = !isDarkTheme
            save()
        }
        if (activity is MainActivity) {
            activity.apply {
                recreateMy()
            }
        }
    }

    fun isSystemNightMode(): Boolean {
        val nightModeFlags: Int =
            appContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }
}