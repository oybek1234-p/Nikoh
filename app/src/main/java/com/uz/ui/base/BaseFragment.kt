package com.uz.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.uz.nikoh.R
import com.uz.ui.MainActivity
import com.uz.ui.utils.hideSoftInput

abstract class BaseFragment<T : ViewDataBinding> : Fragment() {

    abstract val layId: Int

    abstract fun viewCreated(bind: T)

    var binding: T? = null

    fun mainActivity() = if (context is MainActivity) context as MainActivity else null

    var showBottomNav = false

    fun clearBackStack() {

    }

    open fun onPhotoGetResult(paths: List<String>) {}

    fun navigate(@IdRes resId: Int, args: Bundle? = null) {
        findNavController().navigate(
            resId,
            args,
            navOptions = animNavOptions,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layId, container, false)
        viewCreated(binding!!)
        mainActivity()?.showBottomSheet(showBottomNav, true)
        return binding?.root
    }

    override fun onPause() {
        hideSoftInput(requireActivity())
        super.onPause()
    }

    companion object {
        val animNavOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fragment_open_anim)
            .setExitAnim(R.anim.fragment_close_anim)
            .setPopEnterAnim(R.anim.fragment_pop_enter)
            .setPopExitAnim(R.anim.fragment_pop_exit)
            .build()
    }
}
