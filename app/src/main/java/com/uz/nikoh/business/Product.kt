package com.uz.nikoh.business

import com.uz.base.imagekit.ImageData
import com.uz.nikoh.location.LocationData
import com.uz.nikoh.price.Price
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    var id: String,
    var ownerId: String,
    var name: String,
    var about: String,
    var photos: ArrayList<ImageData> = arrayListOf(),
    var video: String,
    var businessName: String,
    var businessPhoto: String,
    var categoryId: String,
    var location: LocationData?,
    var price: Price?
) {
    constructor() : this("", "", "", "", arrayListOf(), "", "", "", "", null, null)
}