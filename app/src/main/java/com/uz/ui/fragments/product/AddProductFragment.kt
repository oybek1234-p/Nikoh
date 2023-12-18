package com.uz.ui.fragments.product

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.uz.base.imagekit.ImageData
import com.uz.base.imagekit.ImageUploader
import com.uz.nikoh.R
import com.uz.nikoh.business.BusinessController
import com.uz.nikoh.business.Product
import com.uz.nikoh.databinding.FragmentAddProductBinding
import com.uz.nikoh.databinding.SmallPhotoItemBinding
import com.uz.nikoh.photo.loadUrl
import com.uz.nikoh.price.Currency
import com.uz.nikoh.price.Price
import com.uz.nikoh.user.CurrentUser
import com.uz.nikoh.utils.TimeUtils
import com.uz.ui.base.BaseAdapter
import com.uz.ui.base.BaseFragment
import com.uz.ui.base.ListClickListener
import com.uz.ui.fragments.photo.PhotoGetFragment
import com.uz.ui.fragments.profile.business.settings.BusinessPhotoEditFragment
import com.uz.ui.utils.visibleOrGone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddProductFragment : BaseFragment<FragmentAddProductBinding>() {

    override val layId: Int
        get() = R.layout.fragment_add_product

    private var productId: String? = null
    private var product: Product? = null
    private var isEdit = false

    companion object {
        const val PRODUCT_ID = "pId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productId = arguments?.getString(PRODUCT_ID).also {
            if (it.isNullOrEmpty().not()) {
                isEdit = true
                product = BusinessController.Products.findMyProduct(it!!)
            }
        }
    }

    private val photoAdapter = object :
        BaseAdapter<ImageData, SmallPhotoItemBinding>(
            R.layout.small_photo_item,
            object : DiffUtil.ItemCallback<ImageData>() {
                override fun areContentsTheSame(oldItem: ImageData, newItem: ImageData): Boolean {
                    return oldItem.url == newItem.url
                }

                override fun areItemsTheSame(oldItem: ImageData, newItem: ImageData): Boolean {
                    return oldItem.id == newItem.id
                }
            }) {
        override fun bind(holder: ViewHolder<*>, model: ImageData, pos: Int) {
            holder.binding.apply {
                (this as SmallPhotoItemBinding)
                photoView.loadUrl(model.url)
            }
        }
    }.apply {
        addClickListener(object : ListClickListener<ImageData>() {
            override fun onClick(holder: BaseAdapter.ViewHolder<*>, model: ImageData) {
                setFragmentResultListener(
                    BusinessPhotoEditFragment.DELETE
                ) { _, _ ->
                    product?.photos?.remove(model)
                    addedPhotos.remove(model)
                    binding?.doneButton?.isEnabled = isValid()
                    submitList(product?.photos)
                }
                navigate(
                    R.id.businessPhotoEditFragment, BusinessPhotoEditFragment.imageData(model)
                )
            }
        })
    }

    private val name: String? get() = binding?.nameView?.editText?.text?.toString()
    private val about: String? get() = binding?.aboutView?.editText?.text?.toString()
    private val price: Long
        get() {
            val priceText = binding?.priceView?.editText?.text?.toString()
            if (priceText.isNullOrEmpty()) {
                return 0L
            }
            return priceText.toLong()
        }

    private var currency: Currency? = null
        set(value) {
            if (field != value) {
                field = value
            }
        }

    private var addedPhotos = arrayListOf<ImageData>()

    private fun addPhotos(photos: List<String>) {
        val productPhotos = product!!.photos
        val photosAdded = photos.map { ImageData(TimeUtils.currentTime().toString(), it, 0, 0) }
        addedPhotos.addAll(photosAdded)

        productPhotos.addAll(0, photosAdded)
        photoAdapter.submitList(productPhotos)
        photoAdapter.notifyDataSetChanged()
        binding?.doneButton?.isEnabled = isValid()
    }

    private var saving = MutableLiveData(false)

    private fun save() {
        if (saving.value == true) return
        saving.postValue(true)
        lifecycleScope.launch {
            val saveRun = {
                product!!.let {
                    it.name = name!!
                    it.price = Price(currency!!.name, price)
                    it.about = about!!
                    it.ownerId = CurrentUser.user.id
                    it.location = CurrentUser.businessOwner?.business?.location
                    it.categoryId = CurrentUser.businessOwner?.business?.categoryId ?: ""
                    it.businessName = CurrentUser.businessOwner?.business?.name ?: ""
                    it.businessPhoto = CurrentUser.businessOwner?.business?.photoMain?.url ?: ""
                    if (it.id.isEmpty()) {
                        it.id = TimeUtils.currentTime().toString()
                    }
                }
                BusinessController.Products.addProduct(product!!, isEdit)
                saving.postValue(false)
                lifecycleScope.launch(Dispatchers.Main) {
                    findNavController().popBackStack()
                }
            }
            if (addedPhotos.size > 0) {
                val uploaded = addedPhotos.map {
                    product?.photos?.remove(it)
                    ImageUploader.uploadImage(it.url).await()
                }
                uploaded.forEach {
                    it.data?.let { d ->
                        product?.photos?.add(d)
                    }
                }
                saveRun.invoke()
            } else {
                saveRun.invoke()
            }
        }
    }

    fun isValid() =
        name.isNullOrEmpty().not() && about.isNullOrEmpty()
            .not() && price != 0L && currency != null && product!!.photos.isNotEmpty()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun viewCreated(bind: FragmentAddProductBinding) {
        bind.apply {
            if (isEdit) {
                toolbar.menu.add(getString(R.string.o_chirish)).setOnMenuItemClickListener {
                    BusinessController.Products.deleteProduct(product!!)
                    findNavController().popBackStack()
                    return@setOnMenuItemClickListener true
                }
            }
            toolbar.setUpBackButton(this@AddProductFragment)
            if (!isEdit) {
                product = Product()
            }
            photoRecyclerView.apply {
                adapter = photoAdapter.apply {
                    if (isEdit) {
                        submitList(product?.photos)
                    }
                }
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
            addImageButton.setOnClickListener {
                PhotoGetFragment.navigate(this@AddProductFragment, true) { photos ->
                    addPhotos(photos)
                }
            }
            usdButton.setOnClickListener {
                currency = Currency.USD
            }
            uzsButton.setOnClickListener {
                currency = Currency.UZS
            }
            nameView.editText?.addTextChangedListener {
                doneButton.isEnabled = isValid()
            }
            aboutView.editText?.addTextChangedListener {
                doneButton.isEnabled = isValid()
            }
            priceView.editText?.addTextChangedListener {
                doneButton.isEnabled = isValid()
            }
            currency = Currency.UZS
            priceChip.check(R.id.uzs_button)
            product?.apply {
                if (isEdit) {
                    toolbar.title = name
                    nameView.editText?.setText(name)
                    aboutView.editText?.setText(about)
                    price?.apply {
                        priceView.editText?.setText(price.toString())
                        currency = Currency.valueOf(currencyId)
                    }
                }
            }
            saving.observe(viewLifecycleOwner) {
                progressBar.visibleOrGone(it)
                doneButton.isEnabled = it.not()
            }
            doneButton.setOnClickListener {
                save()
            }
        }
    }
}