package com.uz.nikoh.locale

import android.app.Activity
import android.content.res.Configuration
import android.content.res.Resources
import com.uz.ui.BaseConfig
import com.uz.ui.MainActivity
import java.util.Locale

object LocaleController {

    val locales = arrayListOf<Locale>().apply {
        add(Locale("uz"))
        add(Locale("ru"))
    }

    fun init(activity: Activity) {
        setLocale(activity, BaseConfig.languageCode, true)
    }


    fun setLocale(activity: Activity, languageCode: String, start: Boolean = false) {
        if (languageCode == BaseConfig.languageCode && !start) return
        BaseConfig.languageCode = languageCode
        BaseConfig.save()
        val locale = locales.find { it.language == languageCode }
        if (locale != null) {
            Locale.setDefault(locale)
        }
        val resources: Resources = activity.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        if (activity is MainActivity) {
            activity.localeChanged()
        }
    }

    fun currentLocale() = locales.find { it.language == BaseConfig.languageCode }

}