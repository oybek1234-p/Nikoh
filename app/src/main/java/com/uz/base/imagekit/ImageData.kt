package com.uz.base.imagekit

import kotlinx.serialization.Serializable

@Serializable
class ImageData(val id: String, var url: String, var width: Int, var height: Int) {
    constructor() : this("", "", 0, 0)
}