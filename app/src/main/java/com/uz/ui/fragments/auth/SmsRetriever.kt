package com.uz.ui.fragments.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetriever.SMS_RETRIEVED_ACTION
import com.uz.nikoh.appContext
import com.uz.nikoh.smsRetrive.SmsRetrieverBroadcast
import com.uz.nikoh.smsRetrive.SmsRetrieverController

class SmsRetriever(
    val context: Context,
    private val result: (code: String, errorMessage: String?) -> Unit
) {

    private var broadcastReceiver: SmsRetrieverBroadcast? = null
    private val smsController = SmsRetrieverController()

    @SuppressLint("NewApi")
    fun start() {
        broadcastReceiver = SmsRetrieverBroadcast(result)
        appContext.registerReceiver(
            broadcastReceiver!!,
            IntentFilter(SMS_RETRIEVED_ACTION),
            SmsRetriever.SEND_PERMISSION, null,
            Context.RECEIVER_NOT_EXPORTED
        )
        smsController.start()
    }

    fun unregister() {
        try {
            appContext.unregisterReceiver(broadcastReceiver)
        }catch (e: Exception) {
            //
        }
    }
}