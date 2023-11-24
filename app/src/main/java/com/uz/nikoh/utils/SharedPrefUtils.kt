package com.uz.nikoh.utils

import android.content.Context
import android.content.SharedPreferences
import com.uz.nikoh.appContext

object SharedPrefUtils {

    fun getSharedPreference(name: String, mode: Int = Context.MODE_PRIVATE): SharedPreferences =
        appContext.getSharedPreferences(name, mode)
}