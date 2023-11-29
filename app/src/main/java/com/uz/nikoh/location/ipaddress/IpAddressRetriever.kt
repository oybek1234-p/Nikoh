package com.uz.nikoh.location.ipaddress

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class IpAddressRetriever {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://ip-api.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(IpService::class.java)

    suspend fun getIpAddress(): IpAddressInfo? = withContext(Dispatchers.Default) {
        val response = service.ipInfo.execute()
        if (response.isSuccessful) {
            return@withContext response.body()
        }
        return@withContext null
    }

}