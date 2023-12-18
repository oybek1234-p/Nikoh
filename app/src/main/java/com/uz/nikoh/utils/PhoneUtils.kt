package com.uz.nikoh.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.EditText
import androidx.core.content.ContextCompat.startActivity
import com.github.vacxe.phonemask.PhoneMaskManager
import com.uz.base.exception.ExceptionHandler
import com.uz.nikoh.appContext


object PhoneUtils {

    const val plus = "+"
    private const val code = "998"
    private const val MASK = "(##) ###-##-##"

    fun getCodeFromSms(message: String): String {
        try {
            return message
                .substringAfter(":")
                .substringBefore(".")
                .trim()
        } catch (e: java.lang.Exception) {
            ExceptionHandler.handle(e)
        }
        return ""
    }

    fun phoneMask(editText: EditText?): PhoneMaskManager? {
        if (editText == null) return null
        return PhoneMaskManager().withMask(MASK)
            .withRegion("$plus$code")
            .bindTo(editText)
    }

    //Formats phone
    fun formatPhoneNumber(phone: String): String {
        var exactPhone = phone.replaceFirst(plus, "")
        if (exactPhone.length > 9) {
            exactPhone = exactPhone.replaceFirst(code, "")
        }
        exactPhone = exactPhone.trim()

        val builder = StringBuilder().apply {
            append(plus)
            append(code)
            append(exactPhone)
        }
        return builder.toString()
    }
//
//    fun parseNumber(phone: String?): String {
//        if (phone.isNullOrEmpty()) return ""
//        val builder = StringBuilder().apply {
//            phone.forEach {
//                if (it.isDigit()) {
//                    append(it)
//                }
//            }
//        }
//        return builder.toString()
//    }

    fun openCallPhone(activity: Activity,phone: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
        activity.startActivity(intent)
    }
}