package com.uz.ui.fragments.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uz.nikoh.business.Business
import com.uz.nikoh.search.Algolia
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    var loading = MutableLiveData(false)
    private var searchText = ""
    val searchList = MutableLiveData<List<Business>?>()
    private val allSearches = mutableMapOf<String, List<Business>?>()
    private var searchJob: Job? = null
    val empty = MutableLiveData(false)

    private fun cancelSearch() {
        searchJob?.cancel()
        searchJob = null
        loading.postValue(false)
        empty.postValue(false)
    }

    override fun onCleared() {
        super.onCleared()
        cancelSearch()
    }

    fun search(text: String) {
        if (searchText == text) return
        searchText = text
        cancelSearch()
        if (text.isEmpty()) {
            searchList.postValue(null)
            return
        }
        if (allSearches[text] != null) {
            val list = allSearches[text]
            searchList.postValue(list)
            empty.postValue(list.isNullOrEmpty())
            return
        }
        searchList.postValue(null)
        empty.postValue(false)
        loading.postValue(true)
        searchJob = viewModelScope.launch {
            delay(1000)
            val result = Algolia.searchBusiness(text, 0, 10, "", false, "")
            if (isActive.not()) return@launch
            loading.postValue(false)
            val list = result.data
            if (result.exception == null) {
                allSearches[text] = list
            }
            searchList.postValue(list)
            empty.postValue(list.isNullOrEmpty())
        }
    }
}