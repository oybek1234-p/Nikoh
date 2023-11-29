package com.uz.ui.fragments.components

import com.uz.nikoh.business.Business
import com.uz.nikoh.business.allPhotosCount
import com.uz.nikoh.databinding.BusinessItemBinding
import com.uz.nikoh.databinding.HeaderItemBinding
import com.uz.nikoh.photo.loadUrl
import com.uz.ui.utils.visibleOrGone

fun HeaderItemBinding.setText(text: String, arrowEnabled: Boolean = false) {
    titleView.text = text
    arrowView.visibleOrGone(arrowEnabled)
}

fun BusinessItemBinding.set(business: Business) {
    val hasPhoto = business.photoMain != null
    addPhotoIcon.visibleOrGone(hasPhoto.not())
    if (business.photoMain != null) {
        photoView.loadUrl(business.photoMain!!.url)
    }
    photoCountView.text = business.allPhotosCount().toString()
    titleView.text = business.name
}