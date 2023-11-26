package com.uz.ui.fragments.settings

import androidx.recyclerview.widget.LinearLayoutManager
import com.uz.nikoh.R
import com.uz.nikoh.databinding.FragmentLanguageBinding
import com.uz.nikoh.databinding.LanguageItemBinding
import com.uz.nikoh.locale.LocaleController
import com.uz.ui.base.BaseFragment
import com.uz.ui.base.EmptyDiffUtil
import com.uz.ui.base.SelectableAdapter
import java.util.Locale

class ChangeLanguageFragment : BaseFragment<FragmentLanguageBinding>() {
    override val layId: Int
        get() = R.layout.fragment_language

    private val languageAdapter = object : SelectableAdapter<Locale, LanguageItemBinding>(
        false,
        R.layout.language_item,
        EmptyDiffUtil()
    ) {
        override fun bind(holder: ViewHolder<*>, model: Locale, pos: Int, selected: Boolean) {
            (holder.binding as LanguageItemBinding).apply {
                checkBox.isChecked = selected
                titleView.text = model.displayName
            }
        }

        override fun onSelected(pos: Int, selected: Boolean) {
            if (selected) {
                val locale = getItem(pos)
                LocaleController.setLocale(requireActivity(), locale.language)
                binding?.toolbar?.title = getString(R.string.til)
            }
        }
    }

    override fun viewCreated(bind: FragmentLanguageBinding) {
        bind.apply {
            toolbar.setUpBackButton(this@ChangeLanguageFragment)
            recyclerView.apply {
                this.adapter = languageAdapter.apply {
                    val locales = LocaleController.locales
                    submitList(locales)
                    setSelected(locales.indexOf(LocaleController.currentLocale()))
                }
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            }
        }
    }
}