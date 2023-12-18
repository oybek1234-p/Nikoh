package com.uz.ui.fragments.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uz.nikoh.business.BusinessController
import com.uz.nikoh.category.Categories
import com.uz.nikoh.location.LocationHelper
import com.uz.nikoh.search.Algolia
import com.uz.ui.fragments.home.FeedAdapter
import com.uz.ui.utils.showToast
import com.uz.ui.utils.update
import kotlinx.coroutines.launch

class SearchResultsViewModel : ViewModel() {

    val loading = MutableLiveData(false)
    val empty = MutableLiveData(false)
    val searchListLive = MutableLiveData<ArrayList<FeedAdapter.FeedModel>>(arrayListOf())

    val searchList get() = searchListLive.value!!
    var loadPage = 0
    var searchText = ""
    var categoryId: String? = null

    var filter: BusinessController.Business.BusinessFilter =
        BusinessController.Business.BusinessFilter.Mix

    fun reloadList() {
        loadPage = 0
        loading.value = false
        searchList.clear()
        searchListLive.update()
        loadList()
    }

    var searchProduct = true

    fun loadList() {
        if (loading.value == true) return
        if (categoryId.isNullOrEmpty().not()) {
            searchProduct = Categories.getCategory(categoryId)?.isByProduct ?: searchProduct
        }
        loading.postValue(true)
        loading.value = true
        empty.postValue(false)
        viewModelScope.launch {
            val response = if (searchProduct.not()) Algolia.searchBusiness(
                searchText,
                loadPage,
                8,
                categoryId ?: "",
                true,
                LocationHelper.getSearchLocationCity().name,
                filter
            ) else Algolia.searchProducts(
                searchText,
                loadPage,
                8,
                categoryId ?: "",
                true,
                LocationHelper.getSearchLocationCity().name,
                filter
            )
            loadPage += 1
            val list = response.data
            if (list.isNullOrEmpty().not()) {
                searchList.addAll(list!!.map {
                    FeedAdapter.FeedModel(
                        if (searchProduct) FeedAdapter.VIEW_TYPE_PRODUCT else FeedAdapter.VIEW_TYPE_BUSINESS,
                        it
                    )
                })
            }
            loading.postValue(false)
            searchListLive.update()
            empty.postValue(searchList.isEmpty())
        }
    }
}