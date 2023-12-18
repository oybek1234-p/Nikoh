package com.uz.nikoh.search

import com.algolia.search.client.ClientSearch
import com.algolia.search.client.Index
import com.algolia.search.dsl.filters
import com.algolia.search.helper.deserialize
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.Attribute
import com.algolia.search.model.IndexName
import com.algolia.search.model.search.Query
import com.uz.nikoh.business.Business
import com.uz.nikoh.business.BusinessController
import com.uz.nikoh.business.Product
import com.uz.nikoh.location.City
import com.uz.ui.utils.showToast
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer

object Algolia {

    private val applicationID = ApplicationID("FSLZQ7YZ34")
    private val apiKey = APIKey("8065bdceb9b9dacee6d772e2f68c8878")

    private val client = ClientSearch(
        applicationID = applicationID,
        apiKey = apiKey
    )

    private val businessIndex: Index by lazy {
        client.initIndex(IndexName("businesses"))
    }

    private val businessPriceDesc: Index by lazy {
        client.initIndex(IndexName("business_price_asc"))
    }

    private val businessTop: Index by lazy {
        client.initIndex(IndexName("business_top"))
    }

    private val products: Index by lazy {
        client.initIndex(IndexName("products"))
    }

    data class SearchResult<T>(
        val data: List<T>?,
        val exception: Exception?,
        val isFullSearch: Boolean,
        val pageNum: Int
    )

    @OptIn(InternalSerializationApi::class)
    suspend fun searchProducts(
        text: String,
        loadPage: Int,
        limit: Int,
        categoryId: String,
        fullSearch: Boolean,
        city: String,
        filter: BusinessController.Business.BusinessFilter = BusinessController.Business.BusinessFilter.Mix
    ): SearchResult<Product> {
        val query = Query().apply {
            query = text
            hitsPerPage = limit
            page = loadPage
            if (categoryId.isNotEmpty() || city.isNotEmpty() && city != City.UZBEKISTAN.name) {
                filters {
                    and {
                        if (categoryId.isNotEmpty()) {
                            facet("categoryId", categoryId)
                        }
                        if (city.isNotEmpty() && city != City.UZBEKISTAN.name) {
                            facet("location.city", city)
                        }
                    }
                }
            }

            if (fullSearch.not()) {
                attributesToRetrieve = listOf(
                    Attribute("name"),
                    Attribute("categoryId")
                )
            }
        }
        val index = products
        val response = index.search(query)
        val result = response.hits.deserialize(Product::class.serializer())

        if (fullSearch) {
            result.forEach { _ ->
                //Put products
            }
        }
        return SearchResult(result, null, fullSearch, response.page)
    }

    @OptIn(InternalSerializationApi::class)
    suspend fun searchBusiness(
        text: String,
        loadPage: Int,
        limit: Int,
        categoryId: String,
        fullSearch: Boolean,
        city: String,
        filter: BusinessController.Business.BusinessFilter = BusinessController.Business.BusinessFilter.Mix
    ): SearchResult<Business> {
        val query = Query().apply {
            query = text
            hitsPerPage = limit
            page = loadPage
            if (categoryId.isNotEmpty() || city.isNotEmpty() && city != City.UZBEKISTAN.name) {
                filters {
                    and {
                        if (categoryId.isNotEmpty()) {
                            facet("categoryId", categoryId)
                        }
                        if (city.isNotEmpty() && city != City.UZBEKISTAN.name) {
                            facet("location.city", city)
                        }
                    }
                }
            }

            if (fullSearch.not()) {
                attributesToRetrieve = listOf(
                    Attribute("name"),
                    Attribute("photoMain"),
                    Attribute("categoryId")
                )
            }
        }
        val index = when (filter) {
            BusinessController.Business.BusinessFilter.Populars ->
                businessTop

            BusinessController.Business.BusinessFilter.Mix ->
                businessIndex

            BusinessController.Business.BusinessFilter.PriceCheap ->
                businessPriceDesc
        }
        val response = index.search(query)
        val result = response.hits.deserialize(Business::class.serializer())
        if (fullSearch) {
            result.forEach {
                BusinessController.Business.putBusiness(it)
            }
        }
        return SearchResult(result, null, fullSearch, response.page)
    }
}