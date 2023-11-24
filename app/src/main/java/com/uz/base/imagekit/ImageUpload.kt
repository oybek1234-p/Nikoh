package com.uz.base.imagekit

import android.graphics.BitmapFactory
import java.io.File

object ImageUploader {

    private const val folder = "files"

    private fun fileName(fileName: String?) =
        if (fileName == null) (ImageKitUtils.imageKitIndex + System.nanoTime() + ".img")
        else ImageKitUtils.imageKitIndex + fileName

    fun uploadFile(
        path: String,
        video: Boolean,
        fileName: String? = null,
        callback: (name: String?) -> Unit
    ) {

    }

    private fun getPhotoSize(file: File): Pair<Int, Int> {
        val bitmap = BitmapFactory.decodeFile(file.path)
        val width = bitmap.width
        val height = bitmap.height
        bitmap.recycle()
        return Pair(width, height)
    }

}