package com.uz.ui.fragments.profile.client.newbusines

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uz.nikoh.user.CurrentUser
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class NewBusinessViewModel : ViewModel() {

    var name = MutableLiveData<String>()
    var categoryId = MutableLiveData<String>()
    var loading = MutableLiveData(false)

    suspend fun changeToBusiness() = suspendCoroutine<Boolean> { c ->
        loading.postValue(true)
        CurrentUser.changeToBusiness(name.value.toString(), categoryId.value.toString()) {
            loading.postValue(false)
            c.resume(it)
        }
    }
}