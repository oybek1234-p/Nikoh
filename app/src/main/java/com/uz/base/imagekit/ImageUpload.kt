package com.uz.base.imagekit

import android.net.Uri
import com.imagekit.android.entity.UploadError
import com.libraryPro.imagekit.entity.UploadResponse
import com.uz.base.data.firebase.DataResult
import com.uz.base.exception.ExceptionHandler
import com.uz.nikoh.appContext
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID.randomUUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ImageUploader {

    private fun newFileName() =
        (ImageKitUtils.imageKitIndex + System.nanoTime())

    private suspend fun compressImage(path: String): File {
        val file = File(path)
        return Compressor.compress(appContext, file)
    }

    private fun deleteFile(file: File) {
        try {
            if (!file.exists()) return
            file.delete()
        } catch (e: Exception) {
            ExceptionHandler.handle(e)
        }
    }

    private suspend fun getRealFile(path: String) =
        withContext(Dispatchers.IO) {
            try {
                val uri = Uri.parse(path)
                val inputStream = appContext.contentResolver.openInputStream(uri)
                val file = File(
                    appContext.cacheDir,
                    randomUUID().toString()
                )
                if (file.exists().not()) {
                    file.createNewFile()
                }
                val outPutStream = FileOutputStream(file)
                inputStream?.copyTo(outPutStream)
                inputStream?.close()
                outPutStream.flush()
                outPutStream.close()
                file
            } catch (e: Exception) {
                ExceptionHandler.handle(e)
                null
            }
        }

    suspend fun uploadImage(
        path: String
    ) = withContext(Dispatchers.IO) {
        async {
            val realFile =
                getRealFile(path) ?: return@async DataResult<ImageData>(null, false, null)
            val compressed = compressImage(realFile.path)
            val delete = async {
                deleteFile(realFile)
            }
            suspendCoroutine { c ->
                ImageKit.getInstance().uploader().upload(
                    compressed,
                    newFileName(),
                    folder = ImageKitUtils.folder,
                    useUniqueFilename = false,
                    imageKitCallback = object : ImageKitCallback {
                        override fun onError(uploadError: UploadError) {
                            delete.start()
                            c.resume(DataResult(null, false, Exception(uploadError.message)))
                        }

                        override fun onSuccess(uploadResponse: UploadResponse?) {
                            delete.start()
                            if (uploadResponse != null) {
                                val imageData = ImageData(
                                    System.nanoTime().toString(),
                                    uploadResponse.name,
                                    uploadResponse.width,
                                    uploadResponse.height
                                )
                                c.resume(DataResult(imageData, true, null))
                            } else {
                                c.resume(DataResult(null, false, null))
                            }
                        }
                    })
            }
        }
    }
}