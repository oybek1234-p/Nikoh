package com.uz.ui.fragments.auth

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.uz.nikoh.R
import com.uz.nikoh.databinding.FragmentVerifyBinding
import com.uz.nikoh.user.CurrentUser
import com.uz.ui.base.BaseFragment
import com.uz.ui.fragments.getuserinfo.GetUserNameFragment
import com.uz.ui.utils.showKeyboard
import com.uz.ui.utils.showToast
import com.uz.ui.utils.visibleOrGone
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class VerifyFragment : BaseFragment<FragmentVerifyBinding>() {

    override val layId: Int
        get() = R.layout.fragment_verify

    private val viewModel: AuthViewModel by viewModels()
    private var code: String? = null
    private var phone: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        code = arguments?.getString(AuthFragment.VERIFY_CODE)
        phone = arguments?.getString(AuthFragment.PHONE)
    }

    override fun onResume() {
        super.onResume()
        binding?.phoneView?.editText?.showKeyboard()
    }

    private val inputCode: String? get() = binding?.phoneView?.editText?.text?.toString()
    private fun isCodeValid() = inputCode?.length == 6

    @SuppressLint("SetTextI18n")
    override fun viewCreated(bind: FragmentVerifyBinding) {
        bind.apply {
            toolbar.setUpBackButton(this@VerifyFragment)
            textView.text = "$phone ${getString(R.string.sms_kod_yuborildi)}"

            viewModel.apply {
                countTime.observe(viewLifecycleOwner) {
                    val finished = it == 0L
                    resendCodeButton.isEnabled = finished
                    timeOutCountView.text = "$it s"
                    timeOutCountView.visibleOrGone(finished.not())
                }
                startTimeOut()

                isResending.observe(viewLifecycleOwner) {
                    progressBar.visibleOrGone(it)
                    resendCodeButton.isEnabled = it.not()
                }
                loading.observe(viewLifecycleOwner) {
                    if (isResending.value == true) return@observe
                    progressBar.visibleOrGone(it)
                    phoneView.isEnabled = it.not()
                    continueButton.isEnabled = it.not() && isCodeValid()
                }
                errorText.observe(viewLifecycleOwner) {
                    val showError = it.isNullOrEmpty().not()
                    phoneView.isErrorEnabled = showError
                    phoneView.error = it
                    phoneView.editText?.showKeyboard()
                }
                autoCodeReceiver.observe(viewLifecycleOwner) {
                    phoneView.editText?.setText(it)
                    verify(it)
                }
            }
            continueButton.setOnClickListener {
                verify(inputCode)
            }

            phoneView.editText?.addTextChangedListener {
                continueButton.isEnabled = isCodeValid()
                viewModel.errorText.postValue(null)
            }
            resendCodeButton.setOnClickListener {
                resendCode()
            }
        }
    }

    private fun resendCode() {
        lifecycleScope.launch {
            viewModel.apply {
                val code = resendCode(requireActivity(), phone!!)
                if (isActive.not()) return@launch
                this@VerifyFragment.code = code
                if (code.isNullOrEmpty().not()) {
                    startTimeOut()
                    binding?.phoneView?.editText?.showKeyboard()
                    showToast(getString(R.string.kod_qayta_yuborildi))
                }
            }
        }
    }

    private fun applyNewUser() {
        lifecycleScope.launch {
            binding.apply {
                val applied = viewModel.applyNewUser()
                if (isActive.not()) return@launch
                if (applied) {
                    val user = CurrentUser.user
                    if (user.name.isEmpty()) {
                        navigate(R.id.action_verifyFragment_to_getUserNameFragment,
                            Bundle().apply {
                                putBoolean(
                                    GetUserNameFragment.BACK_ENABLED, false
                                )
                            })
                    } else {
                        mainActivity()?.showBaseScreen()
                    }
                }
            }
        }
    }

    private fun verify(inputCode: String?) {
        if (inputCode.isNullOrEmpty() || code.isNullOrEmpty()) return
        lifecycleScope.launch {
            val success = viewModel.verifyCode(requireContext(), inputCode, code!!)
            if (isActive.not()) return@launch
            if (success) {
                applyNewUser()
            }
        }
    }
}