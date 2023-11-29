package com.uz.ui.fragments.profile.business.settings

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.uz.nikoh.R
import com.uz.nikoh.databinding.FragmentBusinessPhotoEditBinding
import com.uz.nikoh.photo.loadUrl
import com.uz.nikoh.user.CurrentUser
import com.uz.ui.base.BaseFragment
import kotlinx.coroutines.launch

class BusinessPhotoEditFragment : BaseFragment<FragmentBusinessPhotoEditBinding>() {
    override val layId: Int
        get() = R.layout.fragment_business_photo_edit

    private var photoId: String? = null

    companion object {
        const val PHOTO_ID = "photoId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photoId = arguments?.getString(PHOTO_ID, "")
    }

    override fun viewCreated(bind: FragmentBusinessPhotoEditBinding) {
        bind.apply {
            toolbar.setUpBackButton(this@BusinessPhotoEditFragment)
            val imageData =
                CurrentUser.businessOwner?.business?.photos?.find { it.id == photoId } ?: return
            imageView.loadUrl(imageData.url, fullQuality = true)
            deleteButton.setOnClickListener {
                lifecycleScope.launch {
                    CurrentUser.businessOwner?.removePhoto(imageData)
                    findNavController().popBackStack()
                }
            }
        }
    }
}