package com.uz.ui

import com.uz.nikoh.utils.SharedPrefUtils

object BaseConfig {

    private val preference = SharedPrefUtils.getSharedPreference("baseConfig")
    var languageCode = ""
    private const val DEFAULT_LANGUAGE_CODE = "uz"
    var isDarkTheme = false

    fun get() {
        preference.apply {
            languageCode = getString("language", DEFAULT_LANGUAGE_CODE) ?: ""
            isDarkTheme = getBoolean("dark", AppTheme.isSystemNightMode())
        }
    }

    init {
        get()
    }

    fun save() {
        preference.edit().apply {
            putString("language", languageCode)
            putBoolean("dark", isDarkTheme)
            apply()
        }
    }
}