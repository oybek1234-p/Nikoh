package com.uz.nikoh.business

import com.uz.base.imagekit.ImageData
import com.uz.nikoh.location.LocationData
import com.uz.nikoh.user.UserMini
import kotlinx.serialization.Serializable

/**
 * Business info
 * adminId - user id
 */
@Serializable
class Business {

    var id = ""

    var admin: UserMini? = null

    var name = ""
    var about = ""

    var photosCount = 0

    var photoMain: ImageData? = null

    var photos = arrayListOf<ImageData>()

    var videoUrl = ""

    var categoryId = ""

    var subCategoryIds: List<String>? = null
    var featureIds: List<String>? = null

    var rating: BusinessRating? = null
    var location: LocationData? = null

    var viewed = 0

    var productsCount = 0

    var telegramLink: String? = null
    var instagramLink: String? = null
    var youTubeLink: String? = null
}

fun Business.allPhotosCount() = photosCount + if (photoMain != null) 1 else 0