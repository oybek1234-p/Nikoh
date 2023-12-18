package com.uz.ui.fragments.product

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.uz.base.imagekit.ImageData
import com.uz.nikoh.R
import com.uz.nikoh.business.BusinessController
import com.uz.nikoh.business.Product
import com.uz.nikoh.databinding.FragmentViewProductBinding
import com.uz.nikoh.price.priceText
import com.uz.ui.base.BaseAdapter
import com.uz.ui.base.BaseFragment
import com.uz.ui.base.ListClickListener
import com.uz.ui.fragments.photo.PhotoShow
import com.uz.ui.fragments.profile.business.settings.PhotosAdapter
import com.uz.ui.utils.makeExpandable

class ViewProductFragment : BaseFragment<FragmentViewProductBinding>() {
    override val layId: Int
        get() = R.layout.fragment_view_product

    private var productId = ""
    private var product: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productId = arguments?.getString(Product::id.name, "") ?: ""
        product = BusinessController.Products.findMyProduct(productId)
    }

    private val photosAdapter = PhotosAdapter().apply {
        addClickListener(object : ListClickListener<ImageData>() {
            override fun onClick(holder: BaseAdapter.ViewHolder<*>, model: ImageData) {
                PhotoShow.showPhotos(this@ViewProductFragment, product!!.photos, model)
            }
        })
    }

    override fun viewCreated(bind: FragmentViewProductBinding) {
        bind.apply {
            backButton.setOnClickListener {
                findNavController().popBackStack()
            }
            if (product == null) return
            val product = product!!
            viewPager.apply {
                adapter = photosAdapter.apply {
                    submitList(product.photos)
                }
                setPageTransformer { page, position ->
                    photoCountView.text = "${currentItem + 1} / ${product.photos.size}"
                }
                dotsIndicator.attachTo(this)
            }
            titleView.text = product.name
            aboutView.apply {
                makeExpandable()
                text = product.about
            }
            priceView.text = product.price?.priceText()
        }
    }
}