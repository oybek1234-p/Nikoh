package com.uz.ui.fragments.profile.business.settings

import android.annotation.SuppressLint
import androidx.core.view.postDelayed
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.uz.base.imagekit.ImageData
import com.uz.nikoh.R
import com.uz.nikoh.business.allPhotosText
import com.uz.nikoh.databinding.BusinessPhotoViewBinding
import com.uz.nikoh.databinding.FragmentBusinessPhotosBinding
import com.uz.nikoh.photo.loadUrl
import com.uz.nikoh.user.CurrentUser
import com.uz.ui.base.BaseAdapter
import com.uz.ui.base.BaseFragment
import com.uz.ui.base.ListClickListener
import com.uz.ui.fragments.photo.PhotoGetFragment
import com.uz.ui.utils.visibleOrGone
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class BusinessPhotosFragment : BaseFragment<FragmentBusinessPhotosBinding>() {

    override val layId: Int
        get() = R.layout.fragment_business_photos

    private val adapterP = PhotosAdapter().apply {
        addClickListener(object : ListClickListener<ImageData>() {
            override fun onClick(holder: BaseAdapter.ViewHolder<*>, model: ImageData) {
                setFragmentResultListener(BusinessPhotoEditFragment.DELETE) { requestKey, bundle ->
                    deletePhoto(model)
                }
                navigate(R.id.businessPhotoEditFragment, BusinessPhotoEditFragment.imageData(model))
            }
        })
    }

    fun deletePhoto(photo: ImageData) {
        lifecycleScope.launch {
            CurrentUser.businessOwner?.removePhoto(photo)
            findNavController().popBackStack()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun viewCreated(bind: FragmentBusinessPhotosBinding) {
        bind.apply {
            toolbar.setUpBackButton(this@BusinessPhotosFragment)
            val businessOwner = CurrentUser.businessOwner!!

            businessOwner.businessLive.observe(viewLifecycleOwner) {
                it?.photoMain?.let { d -> photoViewMain.loadUrl(d.url) }
                photoViewMain.visibleOrGone(it?.photoMain != null)
                buttonAddMainPhoto.text =
                    if (it?.photoMain == null) getString(R.string.qo_shish) else getString(
                        R.string.o_zgartirish
                    )
            }
            businessOwner.photosLive.observe(viewLifecycleOwner) {
                val list = it?.toMutableList()
                photosHeader.text = businessOwner.business!!.allPhotosText()
                adapterP.submitList(list)
            }
            recyclerViewPhotos.apply {
                adapter = adapterP
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
            buttonAddPhotos.setOnClickListener {
                PhotoGetFragment.navigate(this@BusinessPhotosFragment, true) { it ->
                    it.firstOrNull() ?: return@navigate
                    it.forEach {
                        GlobalScope.launch {
                            businessOwner.addImage(it)
                        }
                    }
                    recyclerViewPhotos.apply {
                        postDelayed(100) {
                            smoothScrollToPosition(0)
                        }
                    }
                }
            }

            buttonAddMainPhoto.setOnClickListener {
                PhotoGetFragment.navigate(this@BusinessPhotosFragment, false) {
                    val imagePath = it.firstOrNull() ?: return@navigate
                    GlobalScope.launch {
                        businessOwner.setMainPhoto(imagePath)
                    }
                }
            }
        }
    }

}

class PhotosAdapter(val fullQuality: Boolean = false) :
    BaseAdapter<ImageData, BusinessPhotoViewBinding>(
        R.layout.business_photo_view, object : DiffUtil.ItemCallback<ImageData>() {
            override fun areContentsTheSame(oldItem: ImageData, newItem: ImageData): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areItemsTheSame(oldItem: ImageData, newItem: ImageData): Boolean {
                return oldItem == newItem
            }
        }
    ) {
    override fun bind(holder: ViewHolder<*>, model: ImageData, pos: Int) {
        holder.binding.apply {
            if (this is BusinessPhotoViewBinding) {
                imageView.loadUrl(model.url, fullQuality = fullQuality)
            }
        }
    }
}