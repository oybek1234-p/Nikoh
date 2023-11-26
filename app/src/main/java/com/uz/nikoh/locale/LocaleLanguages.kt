package com.uz.nikoh.locale

import com.uz.nikoh.R
import com.uz.nikoh.appContext

enum class LocaleLanguages(val localeName: String, val localeCode: String) {
    UZBEK(appContext.getString(R.string.uzbek), "uz"),
    RUSSIAN(appContext.getString(R.string.ruscha),"ru")
}