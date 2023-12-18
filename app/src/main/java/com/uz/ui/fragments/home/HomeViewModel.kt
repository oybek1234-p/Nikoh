package com.uz.ui.fragments.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    val feedListLive = MutableLiveData<ArrayList<FeedAdapter.FeedModel>>().apply {
        value = arrayListOf()
    }
    val feedList = feedListLive.value!!

}