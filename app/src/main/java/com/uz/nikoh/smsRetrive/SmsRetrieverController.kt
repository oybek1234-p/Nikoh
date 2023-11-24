package com.uz.nikoh.smsRetrive

import com.google.android.gms.auth.api.phone.SmsRetriever
import com.uz.nikoh.appContext

class SmsRetrieverController {

    private val smsRetriever by lazy {
        SmsRetriever.getClient(appContext)
    }

    fun start() {
        start {}
    }

    private fun start(done: (success: Boolean) -> Unit) {
        val task = smsRetriever.startSmsRetriever()
        task.addOnCompleteListener {
            done.invoke(it.isSuccessful)
        }
    }

}