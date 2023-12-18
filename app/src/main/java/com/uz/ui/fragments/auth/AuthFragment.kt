package com.uz.ui.fragments.auth

import android.content.pm.ApplicationInfo
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.vacxe.phonemask.PhoneMaskManager
import com.uz.nikoh.R
import com.uz.nikoh.appContext
import com.uz.nikoh.databinding.FragmentAuthBinding
import com.uz.nikoh.user.CurrentUser
import com.uz.nikoh.utils.PhoneUtils
import com.uz.ui.base.BaseFragment
import com.uz.ui.utils.showKeyboard
import com.uz.ui.utils.visibleOrGone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AuthFragment : BaseFragment<FragmentAuthBinding>() {
    override val layId: Int
        get() = R.layout.fragment_auth

    private var phoneMask: PhoneMaskManager? = null

    private val phoneText: String? get() = phoneMask?.phone
    private var savedPhone: String? = null

    private val viewModel: AuthViewModel by viewModels()

    private fun phoneValid(): Boolean {
        return (phoneText?.length ?: 0) == 13
    }

    override fun onResume() {
        super.onResume()
        binding?.phoneView?.editText?.showKeyboard()
    }

    override fun onDestroyView() {
        savedPhone = phoneText
        super.onDestroyView()
    }

    override fun viewCreated(bind: FragmentAuthBinding) {
        bind.apply {
            toolbar.setUpBackButton(this@AuthFragment)
            phoneView.apply {
                phoneMask = PhoneUtils.phoneMask(editText)

                editText?.addTextChangedListener {
                    continueButton.isEnabled = phoneValid()
                    viewModel.errorText.postValue(null)
                }
                savedPhone?.let {
                    editText?.setText(it)
                }
            }
            viewModel.apply {
                loading.observe(viewLifecycleOwner) {
                    progressBar.visibleOrGone(it)
                    phoneView.isEnabled = it.not()
                    continueButton.isEnabled = it.not() && phoneValid()
                }
                errorText.observe(viewLifecycleOwner) {
                    val showError = it.isNullOrEmpty().not()
                    phoneView.isErrorEnabled = showError
                    phoneView.error = it
                }
            }
            continueButton.setOnClickListener {
                val isDebuggable =
                    appContext.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
                if (isDebuggable) {
                    lifecycleScope.launch {
                        viewModel.applyNewUser(true, phoneText!!)
                        if (CurrentUser.user.name.isEmpty()) {
                            navigate(R.id.getUserNameFragment)
                        } else {
                            mainActivity()?.showBaseScreen(true)
                        }
                    }
                } else {
                    sendCode()
                }
            }
        }
    }

    private fun sendCode() {
        lifecycleScope.launch(Dispatchers.Main) {
            val code = viewModel.sendCode(requireActivity(), phoneText!!)
            if (isActive.not()) return@launch
            if (code.isNullOrEmpty().not()) {
                val bundle = Bundle().apply {
                    putString(PHONE, phoneText)
                    putString(VERIFY_CODE, code)
                }
                navigate(
                    R.id.action_authFragment_to_verifyFragment,
                    bundle
                )
            }
        }
    }

    companion object {
        const val VERIFY_CODE = "verify_code"
        const val PHONE = "phone"
    }
}