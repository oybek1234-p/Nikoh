package com.uz.nikoh.user

import com.uz.nikoh.location.LatLngMy
import com.uz.nikoh.location.LocationData
import com.uz.nikoh.utils.SharedPrefUtils

class UserConfig {
    var user = User()

    companion object {
        private const val USER_SHARED_PREF = "user_config"
    }

    private val sharedPref = SharedPrefUtils.getSharedPreference(USER_SHARED_PREF)

    init {
        getUserFromCache()
    }

    private fun getUserFromCache() {
        user.id = sharedPref.getString("id", "")!!
        user.name = sharedPref.getString("name", "")!!
        user.photo = sharedPref.getString("photo", "")!!
        user.phone = sharedPref.getString("phone", "")!!
        user.lastSeenAt = sharedPref.getLong("lastSeenAt", 0L)
        user.createdAt = sharedPref.getLong("createdAt", 0L)
        user.savedCount = sharedPref.getInt("savedCount", 0)
        user.isBusiness = sharedPref.getBoolean("isBusiness", false)

        LocationData(
            sharedPref.getString("address", ""),
            LatLngMy(
                sharedPref.getFloat("lat", 0f).toDouble(),
                sharedPref.getFloat("long", 0f).toDouble()
            ),
            sharedPref.getString("city", "")
        ).let {
            user.location = it
        }
    }

    fun saveUser() {
        sharedPref.edit().apply {
            putString("id", user.id)
            putString("name", user.name)
            putString("photo", user.photo)
            putString("phone", user.phone)
            putLong("lastSeenAt", user.lastSeenAt)
            putLong("createdAt", user.createdAt)
            putInt("savedCount", user.savedCount)
            putBoolean("isBusiness", user.isBusiness)
        }.apply()
    }

    fun clear() {
        user = User()
        sharedPref.edit().clear().apply()
    }
}