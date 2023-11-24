package com.uz.nikoh.auth

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.uz.base.data.firebase.firebaseAuth
import com.uz.nikoh.utils.PhoneUtils
import com.uz.ui.utils.showToast
import java.util.concurrent.TimeUnit

//SendCodeSms -> VerifyCode -> onPhoneVerified -> finishAuth
object AuthController {

    private const val LANGUAGE_CODE = "uz"

    fun verifyCode(
        code: String,
        verificationCode: String,
        done: (success: Boolean, exception: Exception?) -> Unit
    ) {
        val credential = PhoneAuthProvider.getCredential(verificationCode!!, code)
        firebaseAuth().signInWithCredential(credential).addOnCompleteListener {
            done.invoke(it.isSuccessful, it.exception)
        }
    }

    fun sendSms(
        activity: Activity,
        phone: String,
        result: (success: Boolean, verificationCode: String?, exception: FirebaseException?) -> Unit
    ) {
        val number = PhoneUtils.formatPhoneNumber(phone)
        firebaseAuth().setLanguageCode(LANGUAGE_CODE)
        val options = PhoneAuthOptions.newBuilder(firebaseAuth())
            .setPhoneNumber(number)
            .setTimeout(TimeOutCounter.TIME_OUT_SEC, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    result.invoke(true, p0.smsCode, null)
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    result.invoke(false, null, p0)
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    result.invoke(true, p0, null)
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

}