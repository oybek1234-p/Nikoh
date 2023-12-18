package com.uz.ui.fragments.photo

import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.stfalcon.imageviewer.StfalconImageViewer
import com.uz.base.imagekit.ImageData
import com.uz.nikoh.photo.loadUrl

object PhotoShow {

    fun showPhotos(fragment: Fragment, list: List<ImageData>, selectedImage: ImageData? = null) {
        val selectIndex = if (selectedImage != null) list.indexOf(selectedImage) else 0
        StfalconImageViewer.Builder(fragment.requireContext(), list) { view, image ->
            view.scaleType = ImageView.ScaleType.FIT_CENTER
            view.loadUrl(image.url,fullQuality = true)
        }.allowZooming(true).withStartPosition(selectIndex).show()
    }
}