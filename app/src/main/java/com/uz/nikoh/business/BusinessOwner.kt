package com.uz.nikoh.business

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.uz.base.data.firebase.increase
import com.uz.base.data.firebase.justResult
import com.uz.base.imagekit.ImageData
import com.uz.base.imagekit.ImageUploader
import com.uz.nikoh.location.LocationData
import com.uz.nikoh.user.CurrentUser
import com.uz.nikoh.user.User
import com.uz.nikoh.utils.JsonUtils
import com.uz.nikoh.utils.SharedPrefUtils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer

class BusinessOwner {

    val user: User get() = CurrentUser.user
    var business: Business? = null
    val businessLive = MutableLiveData(business)

    private val photos get() = business?.photos
    val photosLive = MutableLiveData(photos)

    private fun requireBusiness() = business!!

    private val sharedPrefBusiness = SharedPrefUtils.getSharedPreference("business")
    private val cacheKey = "business"

    fun updateAdmin() {
        businessDocument.update("admin", business?.admin)
    }

    private val businessDocument: DocumentReference
        get() = BusinessController.Business.businessDocument(
            requireBusiness().id
        )

    fun clear() {
        business = null
        save()
    }

    @OptIn(InternalSerializationApi::class)
    private fun get() {
        val businessJsonString = sharedPrefBusiness.getString(cacheKey, null)
        if (businessJsonString.isNullOrEmpty().not()) {
            val businessCache = JsonUtils.json.decodeFromString<Business?>(
                Business::class.serializer(), businessJsonString!!
            )
            if (businessCache != null) {
                business = businessCache
                businessLive.postValue(businessCache)
                photosChanged()
            }
        }
    }

    fun setNameAndAbout(name: String, about: String) {
        val business = requireBusiness()
        business.name = name
        business.about = about
        update(business, false)
        businessDocument.update(
            mapOf(
                Pair(Business::name.name, name), Pair(Business::about.name, about)
            )
        )
    }

    init {
        get()
    }

    @OptIn(InternalSerializationApi::class, DelicateCoroutinesApi::class)
    private fun save() {
        GlobalScope.launch {
            if (business == null) {
                sharedPrefBusiness.edit().clear().apply()
                return@launch
            }
            val jsonString = JsonUtils.json.encodeToString(Business::class.serializer(), business!!)
            sharedPrefBusiness.edit().putString(cacheKey, jsonString).apply()
        }
    }

    fun update(business: Business, network: Boolean, done: ((s: Boolean) -> Unit)? = null) {
        this.business = business
        businessLive.postValue(business)
        photosChanged()
        save()

        if (network) {
            BusinessController.Business.businessDocument(business.id).set(business)
                .justResult { s ->
                    done?.invoke(s)
                }
        }
    }

    private val uploadingPhotos = mutableSetOf<String>()

    private fun photosChanged() {
        photosLive.postValue(photos)
    }

    suspend fun setMainPhoto(imagePath: String) {
        if (uploadingPhotos.contains(imagePath)) return

        val tempImage = ImageData(System.nanoTime().toString(), imagePath, -1, -1)
        val oldImage = requireBusiness().photoMain
        requireBusiness().photoMain = tempImage
        businessLive.postValue(requireBusiness())

        val imageResult = ImageUploader.uploadImage(imagePath).await()
        val imageData = imageResult.data
        requireBusiness().photoMain = imageData ?: oldImage
        businessLive.postValue(requireBusiness())
        businessDocument.update(Business::photoMain.name, imageData)
        save()
    }

    suspend fun addImage(imagePath: String) {
        if (business == null) return
        val photos = photos!!

        if (uploadingPhotos.contains(imagePath)) return
        uploadingPhotos.add(imagePath)

        val tempImage = ImageData(System.nanoTime().toString(), imagePath, -1, -1)
        photos.add(0, tempImage)
        photosChanged()

        //Upload image ->
        val imageResult = ImageUploader.uploadImage(imagePath).await()
        val loadedImageData = imageResult.data
        uploadingPhotos.remove(imagePath)

        val tempIndex = photos.indexOf(tempImage)
        photos.removeAt(tempIndex)
        if (loadedImageData != null) {
            businessDocument.update(Business::photos.name, FieldValue.arrayUnion(loadedImageData))
            businessDocument.increase(Business::photosCount, true)
            requireBusiness().photosCount++
            photos.add(tempIndex, loadedImageData)
            save()
        }
        photosChanged()
    }

    fun removePhoto(imageData: ImageData) {
        photos!!.remove(imageData)
        photosChanged()
        businessDocument.update(Business::photos.name, FieldValue.arrayRemove(imageData))
            .justResult {
                businessDocument.increase(Business::photosCount, false)
                requireBusiness().photosCount--
                save()
            }
    }

    fun setVideoUrl(videoUrl: String) {
        requireBusiness().videoUrl = videoUrl
        businessLive.postValue(requireBusiness())
        save()
        businessDocument.update(Business::videoUrl.name, videoUrl)
    }

    fun setLocation(location: LocationData) {
        requireBusiness().apply {
            this.location = location
            businessLive.postValue(this)
            save()
            businessDocument.update(Business::location.name, location)
        }
    }

    fun setMediaLinks(
        telegram: String?, instagram: String?, youTube: String?
    ) {
        requireBusiness().apply {
            telegramLink = telegram
            instagramLink = instagram
            youTubeLink = youTube
            save()
            businessLive.postValue(this)
            businessDocument.update(
                mapOf(
                    Pair(Business::telegramLink.name, telegram),
                    Pair(Business::youTubeLink.name, youTube),
                    Pair(Business::instagramLink.name, instagram)
                )
            )
        }
    }

    fun setSubcategories(subcategories: List<String>) {
        requireBusiness().apply {
            this.subCategoryIds = subcategories
            businessLive.postValue(this)
            save()
            businessDocument.update(Business::subCategoryIds.name, subcategories)
        }
    }

    fun setFeatures(features: List<String>) {
        requireBusiness().apply {
            this.featureIds = features
            businessLive.postValue(this)
            save()
            businessDocument.update(Business::featureIds.name, features)
        }
    }

    fun increaseProducts(increase: Boolean) {
        requireBusiness().apply {
            productsCount = if (increase) productsCount + 1 else productsCount - 1
            businessLive.postValue(this)
            save()
            businessDocument.increase(Business::productsCount, increase)
        }
    }

    fun setPrice(updatedPrice: BusinessPrice) {
        requireBusiness().apply {
            price = updatedPrice
            businessLive.postValue(this)
            save()
            businessDocument.update(Business::price.name, updatedPrice)
        }
    }
}