package com.uz.ui.fragments.product

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.uz.nikoh.R
import com.uz.nikoh.business.BusinessController
import com.uz.nikoh.business.Product
import com.uz.nikoh.databinding.BusinessProductItemBinding
import com.uz.nikoh.databinding.FragmentBusinessProductsBinding
import com.uz.nikoh.databinding.ProductHorizontalItemBinding
import com.uz.nikoh.photo.loadUrl
import com.uz.nikoh.price.priceText
import com.uz.ui.base.BaseAdapter
import com.uz.ui.base.BaseFragment
import com.uz.ui.base.ListClickListener
import com.uz.ui.utils.visibleOrGone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BusinessProductsFragment : BaseFragment<FragmentBusinessProductsBinding>() {
    override val layId: Int
        get() = R.layout.fragment_business_products

    private var isEdit = false
    private var ownerId = ""

    companion object {
        const val IS_EDIT = "edit"
        const val OWNER_ID = "ownerId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isEdit = arguments?.getBoolean(IS_EDIT) ?: false
        ownerId = arguments?.getString(OWNER_ID, "")!!
    }

    class ProductAdapter(val horizontal: Boolean) :
        BaseAdapter<Product, ViewDataBinding>(
            R.layout.business_product_item,
            object : DiffUtil.ItemCallback<Product>() {
                override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                    return oldItem == newItem
                }

                override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                    return oldItem.id == newItem.id
                }
            }
        ) {

        override fun getLayoutId(position: Int): Int {
            return if (horizontal) R.layout.product_horizontal_item else R.layout.business_product_item
        }

        override fun bind(holder: ViewHolder<*>, model: Product, pos: Int) {
            holder.binding.apply {
                if (this is BusinessProductItemBinding) {
                    titleView.text = model.name
                    subtitleView.text = model.about
                    priceView.text = model.price?.priceText()
                    model.photos.firstOrNull()?.url?.let {
                        photoView.loadUrl(it)
                    }
                } else {
                    (this as ProductHorizontalItemBinding)
                    titleView.text = model.name
                    priceView.text = model.price?.priceText()
                    model.photos.firstOrNull()?.url?.let {
                        photoView.loadUrl(it)
                    }
                }
            }
        }
    }

    private val adapterM = ProductAdapter(false).apply {
        addClickListener(object : ListClickListener<Product>() {
            override fun onClick(holder: BaseAdapter.ViewHolder<*>, model: Product) {
                if (isEdit) {
                    navigate(R.id.addProductFragment, Bundle().apply {
                        putString(AddProductFragment.PRODUCT_ID, model.id)
                    })
                } else {
                    navigate(R.id.viewProductFragment, Bundle().apply {
                        putString(Product::id.name, model.id)
                    })
                }
            }
        })
    }

    private fun products() = BusinessController.Products.getProducts(ownerId)

    private fun load() {
        binding?.apply {
            progressBar.visibleOrGone(true)
            lifecycleScope.launch {
                BusinessController.Products.loadProducts(ownerId)
                lifecycleScope.launch(Dispatchers.Main) {
                    progressBar.visibleOrGone(false)
                    val loadedProducts = products()
                    emptyView.visibleOrGone(loadedProducts.isEmpty())
                    adapterM.submitList(products())
                }
            }
        }
    }

    override fun viewCreated(bind: FragmentBusinessProductsBinding) {
        bind.apply {
            toolbar.setUpBackButton(this@BusinessProductsFragment)
            addProductButton.visibleOrGone(isEdit)
            recyclerView.apply {
                emptyView.visibleOrGone(false)
                progressBar.visibleOrGone(false)
                adapter = adapterM.apply {
                    val products = products()
                    if (products.isEmpty()) {
                        load()
                    } else {
                        submitList(products)
                    }
                }
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }
            addProductButton.setOnClickListener {
                navigate(R.id.addProductFragment)
            }
        }
    }
}