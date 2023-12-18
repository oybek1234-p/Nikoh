package com.uz.nikoh.api

import com.google.firebase.firestore.Query
import com.uz.base.data.firebase.DataResult
import com.uz.base.data.firebase.fireStoreCollection
import com.uz.nikoh.business.Business
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BusinessApi {
    private var businessReference = fireStoreCollection("businesses")

    fun businessDocument(id: String) = businessReference.document(id)

    suspend fun loadBusiness(id: String) =
        suspendCoroutine { c ->
            businessDocument(id).get().addOnCompleteListener {
                c.resume(it.result.toObject(Business::class.java))
            }
        }

    enum class BusinessFilter {
        Populars,
        None
    }

    suspend fun loadBusinesses(
        startId: String,
        resultsLimit: Long = 8L,
        categoryId: String,
        filter: BusinessFilter,
        city: String
    ) = suspendCoroutine<DataResult<List<Business>?>> { c ->
        var reference = businessReference.limit(resultsLimit)
        if (startId.isNotEmpty()) {
            reference = reference.startAfter(startId)
        }
        if (categoryId.isNotEmpty()) {
            reference =
                reference.whereEqualTo(
                    Business::categoryId.name,
                    categoryId
                )
        }
        if (filter == BusinessFilter.Populars) {
            reference =
                reference.orderBy(
                    Business::rating.name + ".${Business::rating.name}",
                    Query.Direction.DESCENDING
                )
        }
        if (city.isNotEmpty()) {
            reference = reference.whereEqualTo("location.city", city)
        }
        reference.get().addOnCompleteListener {
            val list = it.result.toObjects(Business::class.java)
            c.resume(DataResult(list, it.isSuccessful, it.exception))
        }
    }

}