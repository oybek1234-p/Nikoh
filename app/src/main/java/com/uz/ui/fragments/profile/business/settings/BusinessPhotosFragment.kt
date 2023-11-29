package com.uz.ui.fragments.profile.business.settings

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.view.postDelayed
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.uz.base.imagekit.ImageData
import com.uz.nikoh.R
import com.uz.nikoh.databinding.BusinessAddPhotoViewBinding
import com.uz.nikoh.databinding.FragmentBusinessPhotosBinding
import com.uz.nikoh.photo.loadUrl
import com.uz.nikoh.user.CurrentUser
import com.uz.ui.base.BaseAdapter
import com.uz.ui.base.BaseFragment
import com.uz.ui.base.ListClickListener
import com.uz.ui.fragments.photo.PhotoGetFragment
import kotlinx.coroutines.launch

class BusinessPhotosFragment : BaseFragment<FragmentBusinessPhotosBinding>() {

    override val layId: Int
        get() = R.layout.fragment_business_photos

    private val adapterP = object : BaseAdapter<ImageData, BusinessAddPhotoViewBinding>(
        R.layout.business_add_photo_view, object : DiffUtil.ItemCallback<ImageData>() {
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
                if (this is BusinessAddPhotoViewBinding) {
                    imageView.loadUrl(model.url)
                }
            }
        }
    }.apply {
        addClickListener(object : ListClickListener<ImageData>() {
            override fun onClick(holder: BaseAdapter.ViewHolder<*>, model: ImageData) {
                navigate(R.id.businessPhotoEditFragment, Bundle().apply {
                    putString(BusinessPhotoEditFragment.PHOTO_ID, model.id)
                })
            }
        })
    }

    @SuppressLint("SetTextI18n")
    override fun viewCreated(bind: FragmentBusinessPhotosBinding) {
        bind.apply {
            toolbar.setUpBackButton(this@BusinessPhotosFragment)
            val businessOwner = CurrentUser.businessOwner!!
            businessOwner.businessLive.observe(viewLifecycleOwner) {
                it?.photoMain?.let { d -> photoViewMain.loadUrl(d.url) }

                buttonAddMainPhoto.text =
                    if (it?.photoMain == null) getString(R.string.qo_shish) else getString(
                        R.string.o_zgartirish
                    )
            }
            businessOwner.photosLive.observe(viewLifecycleOwner) {
                val list = it?.toMutableList()
                photosHeader.text = (list?.size ?: 0).toString() + " ${getString(R.string.rasmlar)}"
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
                        lifecycleScope.launch {
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
                    lifecycleScope.launch {
                        businessOwner.setMainPhoto(imagePath)
                    }
                }
            }
        }
    }

}