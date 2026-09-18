package com.landoulsi.catalog.shared.presentation.common

import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals

class ProductFormatterImplTest {

    @Test
    fun `formats price with the default dollar symbol and two decimals in US locale`() {
        val formatter = ProductFormatterImpl(Locale.US)

        assertEquals("$24.99", formatter.formatPrice(24.99))
        assertEquals("$10.00", formatter.formatPrice(10.0))
        assertEquals("$0.50", formatter.formatPrice(0.5))
    }

    @Test
    fun `formats price with comma decimal separator in Germany locale`() {
        val formatter = ProductFormatterImpl(Locale.GERMANY)

        assertEquals("$24,99", formatter.formatPrice(24.99))
        assertEquals("$10,00", formatter.formatPrice(10.0))
    }

    @Test
    fun `formats price with whatever symbol CurrencyProvider supplies`() {
        val formatter = ProductFormatterImpl(
            locale = Locale.US,
            currencyProvider = CurrencyProvider("AED"),
        )

        assertEquals("AED24.99", formatter.formatPrice(24.99))
    }

    @Test
    fun `formats rating with one decimal in US locale`() {
        val formatter = ProductFormatterImpl(Locale.US)

        assertEquals("4.5", formatter.formatRating(4.5))
        assertEquals("5.0", formatter.formatRating(5.0))
        assertEquals("4.6", formatter.formatRating(4.64))
    }

    @Test
    fun `formats rating with comma decimal separator in Germany locale`() {
        val formatter = ProductFormatterImpl(Locale.GERMANY)

        assertEquals("4,5", formatter.formatRating(4.5))
    }

    @Test
    fun `formats discount percentage with no decimals`() {
        val usFormatter = ProductFormatterImpl(Locale.US)
        val deFormatter = ProductFormatterImpl(Locale.GERMANY)

        assertEquals("15", usFormatter.formatDiscount(15.0))
        assertEquals("15", usFormatter.formatDiscount(15.4))
        assertEquals("16", usFormatter.formatDiscount(15.6))
        assertEquals("15", deFormatter.formatDiscount(15.0))
    }

    @Test
    fun `dynamically reads locale from provider lambda`() {
        var currentLocale = Locale.US
        val formatter = ProductFormatterImpl(localeProvider = { currentLocale })

        assertEquals("$99.99", formatter.formatPrice(99.99))

        currentLocale = Locale.GERMANY
        assertEquals("$99,99", formatter.formatPrice(99.99))
    }
}
