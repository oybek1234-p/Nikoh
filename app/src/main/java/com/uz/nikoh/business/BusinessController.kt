package com.uz.nikoh.business

import com.google.firebase.firestore.Query
import com.uz.base.data.firebase.DataResult
import com.uz.base.data.firebase.fireStoreCollection
import com.uz.nikoh.R
import com.uz.nikoh.user.CurrentUser
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object BusinessController {

    object Business {
        private var businessReference = fireStoreCollection("businesses")
        private var businesses = hashMapOf<String, com.uz.nikoh.business.Business>()

        fun putBusiness(business: com.uz.nikoh.business.Business) {
            businesses[business.id] = business
        }

        fun getBusiness(id: String) = businesses[id]

        fun businessDocument(id: String) = businessReference.document(id)

        suspend fun loadBusiness(id: String) =
            suspendCoroutine { c ->
                businessDocument(id).get().addOnCompleteListener {
                    val business = it.result.toObject(com.uz.nikoh.business.Business::class.java)
                    if (business != null) {
                        putBusiness(business)
                    }
                    c.resume(business)
                }
            }

        enum class BusinessFilter(val stringRes: Int) {
            Populars(R.string.mashxurlari),
            PriceCheap(R.string.hamyonbob),
            Mix(R.string.aralash);
        }

        suspend fun loadBusinesses(
            startId: String,
            resultsLimit: Long = 8L,
            categoryId: String,
            filter: BusinessFilter,
            city: String
        ) = suspendCoroutine<DataResult<List<com.uz.nikoh.business.Business>?>> { c ->
            var reference = businessReference.limit(resultsLimit)
            if (startId.isNotEmpty()) {
                reference = reference.startAfter(startId)
            }
            if (categoryId.isNotEmpty()) {
                reference =
                    reference.whereEqualTo(
                        com.uz.nikoh.business.Business::categoryId.name,
                        categoryId
                    )
            }
            if (filter == BusinessFilter.Populars) {
                reference =
                    reference.orderBy(
                        com.uz.nikoh.business.Business::rating.name + ".${com.uz.nikoh.business.Business::rating.name}",
                        Query.Direction.DESCENDING
                    )
            }
            if (city.isNotEmpty()) {
                reference = reference.whereEqualTo("location.city", city)
            }
            reference.get().addOnCompleteListener {
                val list = it.result.toObjects(com.uz.nikoh.business.Business::class.java)
                c.resume(DataResult(list, it.isSuccessful, it.exception))
            }
        }

    }

    object Products {
        private const val PRODUCTS_PATH = "products"

        private val productsReference = fireStoreCollection(PRODUCTS_PATH)
        private val loadingBusinessProducts = mutableSetOf<String>()

        private val products = mutableMapOf<String, ArrayList<Product>>()

        fun getProducts(ownerId: String) = products.getOrPut(ownerId) { arrayListOf() }
        fun findMyProduct(id: String): Product? {
            var product: Product? = null
            products.forEach { (t, u) ->
                product = u.find { it.id == id }
                if (product != null) {
                    return@forEach
                }
            }
            return product
        }

        suspend fun loadProducts(ownerId: String) =
            suspendCoroutine<DataResult<List<Product>>> { c ->
                loadingBusinessProducts.add(ownerId)
                productsReference.whereEqualTo(Product::ownerId.name, ownerId).get()
                    .addOnCompleteListener {
                        val newProducts = it.result.toObjects(Product::class.java)
                        getProducts(ownerId).apply {
                            clear()
                            addAll(newProducts)
                        }
                        val result = DataResult(newProducts, it.isSuccessful, it.exception)
                        c.resume(result)
                    }
            }

        fun addProduct(product: Product, update: Boolean) {
            getProducts(product.ownerId).apply {
                if (update) {
                    remove(product)
                }
                add(0, product)
            }
            productsReference.document(product.id).set(product)
            CurrentUser.businessOwner?.increaseProducts(true)
        }

        fun deleteProduct(product: Product) {
            getProducts(product.ownerId).remove(product)
            productsReference.document(product.id).delete()
            CurrentUser.businessOwner?.increaseProducts(false)
        }
    }
}