package com.uz.nikoh.business

import com.uz.base.data.firebase.fireStoreCollection
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object BusinessController {

    var businessReference = fireStoreCollection("businesses")

    fun businessDocument(id: String) = businessReference.document(id)

    suspend fun loadBusiness(id: String) = suspendCoroutine { c ->
        businessDocument(id).get().addOnCompleteListener {
            c.resume(it.result.toObject(Business::class.java))
        }
    }
}