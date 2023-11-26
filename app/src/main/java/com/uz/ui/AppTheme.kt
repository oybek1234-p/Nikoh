package com.uz.ui

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.uz.nikoh.appContext
import kotlinx.coroutines.delay

object AppTheme {

    val isNightMode: Boolean get() = BaseConfig.isDarkTheme

    fun setTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (isNightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    suspend fun toggleTheme(activity: Activity) {
        BaseConfig.apply {
            isDarkTheme = !isDarkTheme
            save()
        }
        activity.apply {
            delay(500)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) // start same activity
            finish()
            overridePendingTransition(0, 0)
        }
    }

    fun isSystemNightMode(): Boolean {
        val nightModeFlags: Int =
            appContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }
}