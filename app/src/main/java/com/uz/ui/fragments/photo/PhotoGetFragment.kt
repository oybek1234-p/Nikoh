package com.uz.ui.fragments.photo

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.uz.nikoh.R

class PhotoGetFragment : DialogFragment() {

    private val done: (list: List<String>) -> Unit = {
        val result = setResultBundle(it)
        setFragmentResult(RESULT, result)
    }

    private var multiple = false

    companion object {
        private const val MULTIPLE_KEY = "multiple"
        private const val RESULT = "photos_result"

        private fun multiple() = Bundle().apply { putBoolean(MULTIPLE_KEY, true) }

        private fun setResultBundle(list: List<String>) = Bundle().apply {
            putStringArray(RESULT, list.toTypedArray())
        }

        private fun getResult(bundle: Bundle): Array<String> {
            return bundle.getStringArray(RESULT) ?: emptyArray()
        }

        fun navigate(fragment: Fragment, multiple: Boolean, result: (list: List<String>) -> Unit) {
            fragment.setFragmentResultListener(RESULT) { r, bundle ->
                if (r == RESULT) {
                    val list = getResult(bundle).toList()
                    result.invoke(list)
                }
            }
            fragment.findNavController()
                .navigate(R.id.photoGetFragment, if (multiple) multiple() else null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        multiple = arguments?.getBoolean(MULTIPLE_KEY) ?: false
        val pickMedia =
            registerForActivityResult(if (multiple) ActivityResultContracts.PickMultipleVisualMedia() else ActivityResultContracts.PickVisualMedia()) {
                when (it) {
                    is List<*> -> {
                        val uris = it as List<Uri>
                        val images = uris.map { p -> p.toString() }
                        done.invoke(images)
                    }

                    is Uri -> {
                        done.invoke(listOf(it.toString()))
                    }

                    else -> {
                        done.invoke(emptyList())
                    }
                }
                findNavController().popBackStack()
            }
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}