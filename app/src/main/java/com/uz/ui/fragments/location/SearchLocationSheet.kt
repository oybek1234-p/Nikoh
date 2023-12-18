package com.uz.ui.fragments.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.uz.nikoh.R
import com.uz.nikoh.databinding.SearchSheetBindingBinding
import com.uz.nikoh.location.LocationHelper

class SearchLocationSheet : BottomSheetDialogFragment() {

    private var binding: SearchSheetBindingBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SearchSheetBindingBinding.inflate(inflater, container, false).apply {
            addressView.text = getString(LocationHelper.getSearchLocationCity().resIdName)
            addressView.setOnClickListener {
                dismiss()
                findNavController().navigate(R.id.chooseSearchLocationFragment)
            }
            saveButton.setOnClickListener {
                dismiss()
            }
        }
        return binding!!.root
    }
}