package com.landoulsi.catalog.shared.presentation.common

import java.util.Locale

/**
 * Concrete [ProductFormatter] implementation for Android / JVM using [Locale].
 *
 * Defaults to the device's current locale dynamically, or accepts a specific
 * [Locale] in tests.
 */
class ProductFormatterImpl(
    private val currencyProvider: CurrencyProvider = CurrencyProvider(),
    private val localeProvider: () -> Locale = { Locale.getDefault() },
) : ProductFormatter {

    constructor(locale: Locale, currencyProvider: CurrencyProvider = CurrencyProvider()) :
        this(currencyProvider, { locale })

    override fun formatPrice(price: Double): String =
        currencyProvider.symbol + String.format(localeProvider(), "%.2f", price)

    override fun formatRating(rating: Double): String =
        String.format(localeProvider(), "%.1f", rating)

    override fun formatDiscount(discountPercentage: Double): String =
        String.format(localeProvider(), "%.0f", discountPercentage)
}
