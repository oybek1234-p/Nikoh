package com.uz.ui.fragments.profile.business.settings

import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.uz.nikoh.R
import com.uz.nikoh.business.BusinessPrice
import com.uz.nikoh.business.PriceType
import com.uz.nikoh.databinding.FragmentSetBusinessPriceBinding
import com.uz.nikoh.price.Currency
import com.uz.nikoh.user.CurrentUser
import com.uz.ui.base.BaseFragment

class BusinessPriceSetFragment : BaseFragment<FragmentSetBusinessPriceBinding>() {

    override val layId: Int
        get() = R.layout.fragment_set_business_price

    private var businessPrice = CurrentUser.businessOwner?.business?.price ?: BusinessPrice()

    private var priceType = businessPrice.priceType
    private var currency = Currency.valueOf(businessPrice.price.currencyId)
    private val price: Long
        get() = (binding?.priceInputView?.editText?.text?.toString()?.ifEmpty { "0" })?.toLong()
            ?: 0L

    override fun viewCreated(bind: FragmentSetBusinessPriceBinding) {
        bind.apply {
            toolbar.setUpBackButton(this@BusinessPriceSetFragment)

            exactPriceChip.setOnClickListener {
                exactPriceChip.isChecked = true
                minPriceChip.isChecked = false
                priceType = PriceType.EXACT_PRICE
            }
            minPriceChip.setOnClickListener {
                minPriceChip.isChecked = true
                exactPriceChip.isChecked = false
                priceType = PriceType.MIN_PRICE
            }
            val defaultPriceType = businessPrice.priceType
            val defPriceTypeId =
                if (defaultPriceType == PriceType.MIN_PRICE) R.id.min_price_chip else R.id.exact_price_chip
            priceTypeChipLay.check(defPriceTypeId)

            uzsButton.setOnClickListener {
                usdButton.isChecked = false
                uzsButton.isChecked = true
                currency = Currency.UZS
            }
            usdButton.setOnClickListener {
                uzsButton.isChecked = false
                usdButton.isChecked = true
                currency = Currency.USD
            }
            currencyChipGroup.apply {
                val defaultCurrency = businessPrice.price.currencyId
                val currencyId =
                    if (defaultCurrency == Currency.UZS.name) R.id.uzs_button else R.id.usd_button
                check(currencyId)
            }
            priceInputView.editText?.setText(businessPrice.price.price.toString())
            priceInputView.editText?.addTextChangedListener {
                doneButton.isEnabled = price > 0L
            }
            doneButton.setOnClickListener {
                businessPrice.apply {
                    this.priceType = this@BusinessPriceSetFragment.priceType
                    this.price.price = this@BusinessPriceSetFragment.price
                    this.price.currencyId = this@BusinessPriceSetFragment.currency.name
                }
                CurrentUser.businessOwner?.setPrice(businessPrice)
                findNavController().popBackStack()
            }
        }
    }
}