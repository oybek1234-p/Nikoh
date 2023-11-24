package com.uz.base.imagekit.data

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import com.imagekit.android.entity.SignatureResponse
import com.imagekit.android.entity.UploadError
import com.uz.base.imagekit.util.SharedPrefUtil
import com.uz.base.imagekit.retrofit.NetworkManager
import com.uz.base.imagekit.util.LogUtil
import com.uz.base.imagekit.ImageKitCallback
import com.libraryPro.imagekit.entity.UploadResponse
import com.uz.base.imagekit.util.GenerateSignature
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class Repository @Inject constructor(
    private val context: Context,
    private val sharedPrefUtil: SharedPrefUtil,
) {
    companion object {
        private const val DURATION_EXPIRY_MINUTES = 45L
    }

    @SuppressLint("CheckResult")
    fun upload(
        file: Any,
        fileName: String,
        useUniqueFilename: Boolean,
        tags: Array<String>?,
        folder: String?,
        isPrivateFile: Boolean?,
        customCoordinates: String?,
        responseFields: String?,
        imageKitCallback: ImageKitCallback,
    ) {
        val expire = ((System.currentTimeMillis() / 1000) + TimeUnit.MINUTES.toSeconds(
            DURATION_EXPIRY_MINUTES
        ))

        val endPoint = sharedPrefUtil.getClientAuthenticationEndpoint()

        if (endPoint.isBlank()) {
            LogUtil.logError("Upload failed! Authentication endpoint is missing!")
            return imageKitCallback.onError(
                UploadError(
                    exception = true,
                    message = "Auth endpoint missing"
                )
            )
        }

        val publicKey = sharedPrefUtil.getClientPublicKey()
        if (publicKey.isBlank()) {
            LogUtil.logError("Upload failed! Public Key is missing!")
            return imageKitCallback.onError(
                UploadError(
                    exception = true,
                    message = "Public key missing"
                )
            )
        }

        val token = GenerateSignature.getUUID()
        val signatureResponse = SignatureResponse(
            token,
            GenerateSignature.generateHashWithHmac256(
                token + expire.toString(),
                "private_JZ3L13IXnw2Ll10+872CR+YISKk="
            ),
            expire = expire.toInt()
        )

        NetworkManager.getFileUploadCall(
            publicKey,
            signatureResponse,
            file,
            fileName,
            useUniqueFilename,
            tags,
            folder,
            isPrivateFile,
            customCoordinates,
            responseFields
        ).doOnError { e ->
            if (e is retrofit2.HttpException) {
                e.response()?.let {
                    try {
                        imageKitCallback.onError(
                            Gson().fromJson(
                                it.errorBody()!!.string(),
                                UploadError::class.java
                            )
                        )
                    } catch (exception: IllegalStateException) {
                        imageKitCallback.onError(
                            UploadError(
                                exception = true,
                                statusNumber = e.code(),
                                message = e.message()
                            )
                        )
                    }
                }
            } else {
                e.message?.let {
                    imageKitCallback.onError(UploadError(true, message = it))
                } ?: run {
                    imageKitCallback.onError(UploadError(true))
                }
            }
        }.subscribe({
            if (it == null) {
                imageKitCallback.onSuccess(null)
            } else {
                imageKitCallback.onSuccess(Gson().fromJson(it.string(), UploadResponse::class.java))
            }
        }, { LogUtil.logError(it) })
    }

    private fun removeQuotesAndUnescape(uncleanJson: String): String? {
        return uncleanJson.replace("^\"|\"$".toRegex(), "")
    }
}