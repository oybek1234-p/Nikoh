package com.uz.ui.fragments.profile.client.newbusines

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewBusinessViewModel : ViewModel() {

    var name = MutableLiveData<String>()
    var categoryId = MutableLiveData<String>()
    var loading = MutableLiveData(false)
}