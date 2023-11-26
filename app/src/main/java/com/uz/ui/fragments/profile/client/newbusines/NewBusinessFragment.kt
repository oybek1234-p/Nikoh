package com.uz.ui.fragments.profile.client.newbusines

import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.uz.nikoh.R
import com.uz.nikoh.category.Categories
import com.uz.nikoh.databinding.FragmentNewBusinessBinding
import com.uz.ui.base.BaseFragment
import com.uz.ui.fragments.category.CategoryFragment
import com.uz.ui.utils.showKeyboard
import com.uz.ui.utils.showToast

class NewBusinessFragment : BaseFragment<FragmentNewBusinessBinding>() {

    override val layId: Int
        get() = R.layout.fragment_new_business

    private val viewModel: NewBusinessViewModel by viewModels()

    private fun canContinue() =
        viewModel.name.value.isNullOrEmpty().not() && viewModel.categoryId.value.isNullOrEmpty()
            .not()

    override fun onResume() {
        super.onResume()
        if (viewModel.name.value.isNullOrEmpty())
            binding?.nameView?.editText?.showKeyboard()
    }

    override fun viewCreated(bind: FragmentNewBusinessBinding) {
        bind.apply {
            toolbar.setUpBackButton(this@NewBusinessFragment)
            bind.apply {
                viewModel.apply {
                    name.observe(viewLifecycleOwner) {
                        continueButton.isEnabled = canContinue()
                    }
                    categoryId.observe(viewLifecycleOwner) {
                        continueButton.isEnabled = canContinue()

                        val category =
                            if (it != null) Categories.getCategory(Categories.CatsId.valueOf(it))
                            else null

                        chooseCatButton.editText?.setText(category?.name)
                    }
                }
                nameView.editText?.apply {
                    setText(viewModel.name.value)
                    addTextChangedListener { it ->
                        viewModel.name.postValue(it.toString())
                    }
                }
                setFragmentResultListener(CategoryFragment.SELECT) { _, b ->
                    viewModel.categoryId.postValue(b.getString(CategoryFragment.SELECTED_ID))
                }
                chooseCatButton.editText?.apply {
                    isClickable = false
                    isFocusable = false
                    setOnClickListener {
                        navigate(R.id.categoryFragment, Bundle().apply {
                            putBoolean(CategoryFragment.SELECT, true)
                            putString(CategoryFragment.SELECTED_ID, viewModel.categoryId.value)
                        })
                    }
                }
                continueButton.setOnClickListener {
                    //->
                    showToast("Create business")
                }
            }
        }
    }
}