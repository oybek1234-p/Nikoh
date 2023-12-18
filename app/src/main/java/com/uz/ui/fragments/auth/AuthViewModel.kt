package com.uz.ui.fragments.auth

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.uz.nikoh.R
import com.uz.nikoh.appContext
import com.uz.nikoh.auth.AuthController
import com.uz.nikoh.auth.TimeOutCounter
import com.uz.nikoh.user.CurrentUser
import com.uz.ui.utils.showToast
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthViewModel : ViewModel() {

    val loading = MutableLiveData(false)
    val isResending = MutableLiveData(false)
    val errorText = MutableLiveData("")

    private val counter = TimeOutCounter()
    val countTime = counter.timeOutLive

    val autoCodeReceiver = MutableLiveData<String>()

    private var smsRetriever: SmsRetriever? = null

    suspend fun applyNewUser(debug: Boolean = false, phone: String? = null) =
        suspendCoroutine { c ->
            loading.postValue(true)
            viewModelScope.launch {
                try {
                    CurrentUser.applyFirebaseUser(debug, phone) {
                        loading.postValue(false)
                        c.resume(it != null)
                    }
                } catch (e: Exception) {
                    showToast(e.message.toString())
                }
            }
        }

    private fun startAutoCodeReceiver() {
        smsRetriever = SmsRetriever(appContext) { code, errorMessage ->
            if (errorMessage == null && code.isNotEmpty()) {
                autoCodeReceiver.postValue(code)
            }
        }.apply {
            start()
        }
    }

    init {
        startAutoCodeReceiver()
    }

    fun startTimeOut() {
        counter.start()
    }

    override fun onCleared() {
        super.onCleared()
        counter.cancel()
        smsRetriever?.unregister()
        smsRetriever = null
    }

    suspend fun resendCode(activity: Activity, phone: String): String? {
        isResending.postValue(true)
        val code = sendCode(activity, phone)
        isResending.postValue(false)
        return code
    }

    suspend fun sendCode(activity: Activity, phone: String) = suspendCoroutine { c ->
        loading.postValue(true)
        AuthController.sendSms(activity, phone) { success, verificationCode, exception ->
            loading.postValue(false)
            if (exception != null) {
                val text = when (exception) {
                    is FirebaseAuthInvalidCredentialsException -> activity.getString(R.string.trnk)
                    is FirebaseTooManyRequestsException -> activity.getString(R.string.kmukuk)
                    else -> activity.getString(R.string.txtzt)
                }
                errorText.postValue(text)
                c.resume(null)
            } else {
                c.resume(verificationCode)
            }
        }
    }

    suspend fun verifyCode(context: Context, code: String, verificationCode: String) =
        suspendCoroutine { con ->
            loading.postValue(true)
            AuthController.verifyCode(code, verificationCode) { success, exception ->
                loading.postValue(false)
                if (exception != null) {
                    val text = when (exception) {
                        is FirebaseAuthInvalidCredentialsException -> context.getString(R.string.knquk)
                        is FirebaseTooManyRequestsException -> context.getString(R.string.kmukuk)
                        else -> context.getString(R.string.txtzt)
                    }
                    errorText.postValue(text)
                }
                con.resume(success)
            }

        }
}
