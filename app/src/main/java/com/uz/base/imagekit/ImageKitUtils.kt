package com.uz.base.imagekit

import com.imagekit.android.entity.TransformationPosition

object ImageKitUtils {

    const val imageKitIndex = "imageKit"
    private const val thumbSize = 100
    private const val thumbBlur = 8
    private const val downscaleValue = 70
    private const val fullQualityValue = 95

    private const val DEFAULT_FORMAT = ImageKitUrlBuilder.WEBP
    private const val THUMB_FORMAT = ImageKitUrlBuilder.JPEG

    const val endPoint = "https://ik.imagekit.io/startup/files/"

    fun initImageKit(context: android.content.Context) {
        ImageKit.init(
            context = context,
            publicKey = "public_1ATYFWvJtgtudSz9o6oJXifiXX8=",
            urlEndpoint = endPoint,
            transformationPosition = TransformationPosition.PATH,
            authenticationEndpoint = "https://www.pythonanywhere.com/user/oybek1234/files/home/oybek1234/mysite/auth"
        )
    }

    fun isUrlImageKit(url: String): Boolean {
        return url.startsWith(imageKitIndex) || url.startsWith(endPoint)
    }

}