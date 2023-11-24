package com.uz.base.imagekit

import com.imagekit.android.entity.UploadError
import com.libraryPro.imagekit.entity.UploadResponse

interface ImageKitCallback {
    fun onSuccess(uploadResponse: UploadResponse?)
    fun onError(uploadError: UploadError)
}