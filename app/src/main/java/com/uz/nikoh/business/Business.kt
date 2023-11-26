package com.uz.nikoh.business

import com.uz.nikoh.location.LocationData
import com.uz.base.imagekit.ImageData
import com.uz.nikoh.user.UserMini

/**
 * Business info
 * adminId - user id
 */
class Business {

    var id = ""

    var admin: UserMini? = null

    var name = ""
    var about = ""

    var photosCount = 0
    var photoMain: ImageData? = null

    var videoUrl = ""

    var categoryId = ""

    var subCategoryIds: List<Int>? = null

    //Qulayliklar masalan: Wifi, Parkovka
    var comfortIds: List<Int>? = null

    var rating: BusinessRating? = null
    var location: LocationData? = null

    var viewed = 0

    var productsCount = 0

}