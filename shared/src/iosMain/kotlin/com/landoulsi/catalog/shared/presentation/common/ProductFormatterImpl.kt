package com.landoulsi.catalog.shared.presentation.common

import platform.Foundation.NSString
import platform.Foundation.stringWithFormat

/**
 * Concrete [ProductFormatter] implementation for iOS using Foundation's [NSString].
 */
class ProductFormatterImpl(
    private val currencyProvider: CurrencyProvider = CurrencyProvider(),
) : ProductFormatter {

    override fun formatPrice(price: Double): String =
        currencyProvider.symbol + NSString.stringWithFormat("%.2f", price)

    override fun formatRating(rating: Double): String =
        NSString.stringWithFormat("%.1f", rating)

    override fun formatDiscount(discountPercentage: Double): String =
        NSString.stringWithFormat("%.0f", discountPercentage)
}
