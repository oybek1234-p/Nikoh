package com.uz.ui.fragments.settings

import androidx.lifecycle.lifecycleScope
import com.uz.nikoh.R
import com.uz.nikoh.databinding.FragmentSettingsBinding
import com.uz.nikoh.locale.LocaleController
import com.uz.ui.AppTheme
import com.uz.ui.base.BaseFragment
import com.uz.ui.fragments.components.TextInfoButton
import kotlinx.coroutines.launch

class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    override val layId: Int
        get() = R.layout.fragment_settings

    override fun viewCreated(bind: FragmentSettingsBinding) {
        bind.apply {
            toolbar.setUpBackButton(this@SettingsFragment)
            TextInfoButton(requireContext()).apply {
                title =
                    getString(R.string.til) + " - ${LocaleController.currentLocale()?.displayLanguage}"
                container.addView(this)
                setOnClickListener {
                    navigate(R.id.changeLanguageFragment)
                }
            }
            TextInfoButton(requireContext()).apply {
                title = context.getString(R.string.qora_tema)
                container.addView(this)
                checkboxEnabled = true
                isCheckBoxChecked = AppTheme.isNightMode
                setOnClickListener {
                    isCheckBoxChecked = !isCheckBoxChecked
                    lifecycleScope.launch {
                        AppTheme.toggleTheme(requireActivity())
                    }
                }
            }
        }
    }

}