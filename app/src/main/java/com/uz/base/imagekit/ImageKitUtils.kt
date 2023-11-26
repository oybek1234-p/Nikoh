package com.uz.base.imagekit

import com.imagekit.android.entity.TransformationPosition

object ImageKitUtils {

    const val imageKitIndex = "imageKit"
    const val folder = "nikohImages"

    private const val downscaleValue = 70
    private const val fullQualityValue = 95

    private const val DEFAULT_FORMAT = ImageKitUrlBuilder.WEBP

    fun buildUrl(
        fileName: String,
        height: Int = 0,
        width: Int = 0,
        fullQuality: Boolean = false,
        blur: Int = 0,
        format: String = DEFAULT_FORMAT
    ): String {
        var url = ImageKitUrlBuilder.newUrl(fileName)
            .quality(if (fullQuality) fullQualityValue else downscaleValue)
            .format(format)
            .width(width)
            .height(height)
        if (blur > 0) {
            url = url.blur(blur)
        }
        return url.get()
    }

    const val endPoint = "https://ik.imagekit.io/startup/$folder/"

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