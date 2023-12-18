package com.uz.ui.fragments.profile.business.settings

import android.os.Bundle
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.uz.base.imagekit.ImageData
import com.uz.nikoh.R
import com.uz.nikoh.databinding.FragmentBusinessPhotoEditBinding
import com.uz.nikoh.photo.loadUrl
import com.uz.nikoh.utils.JsonUtils
import com.uz.ui.base.BaseFragment
import kotlinx.serialization.encodeToString

class BusinessPhotoEditFragment : BaseFragment<FragmentBusinessPhotoEditBinding>() {
    override val layId: Int
        get() = R.layout.fragment_business_photo_edit

    private var imageData: ImageData? = null

    companion object {
        const val PHOTO = "photo"
        const val DELETE = "delete"

        fun imageData(imageDaData: ImageData) = Bundle().apply {
            putString(PHOTO, JsonUtils.json.encodeToString(imageDaData))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imageJson = arguments?.getString(PHOTO, "")
        if (imageJson.isNullOrEmpty().not()) {
            imageData = JsonUtils.json.decodeFromString(imageJson!!)
        }
    }

    override fun viewCreated(bind: FragmentBusinessPhotoEditBinding) {
        bind.apply {
            toolbar.setUpBackButton(this@BusinessPhotoEditFragment)
            if (imageData == null) return
            imageView.loadUrl(imageData!!.url, fullQuality = true)
            deleteButton.setOnClickListener {
                setFragmentResult(DELETE, Bundle())
                findNavController().popBackStack()
            }
        }
    }
}