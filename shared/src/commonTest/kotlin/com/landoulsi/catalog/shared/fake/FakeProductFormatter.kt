package com.landoulsi.catalog.shared.fake

import com.landoulsi.catalog.shared.presentation.common.ProductFormatter
import kotlin.math.abs
import kotlin.math.roundToLong

/**
 * Platform-independent [ProductFormatter] for tests in commonTest.
 */
class FakeProductFormatter(
    private val priceTransform: (Double) -> String = { price ->
        val cents = (price * 100).roundToLong()
        val dollars = cents / 100
        val remainder = abs(cents % 100)
        "$dollars.${remainder.toString().padStart(2, '0')}"
    },
    private val ratingTransform: (Double) -> String = { rating ->
        val tenths = (rating * 10).roundToLong()
        val ones = tenths / 10
        val remainder = abs(tenths % 10)
        "$ones.$remainder"
    },
    private val discountTransform: (Double) -> String = { discount ->
        "${discount.roundToLong()}"
    },
) : ProductFormatter {

    override fun formatPrice(price: Double): String = priceTransform(price)

    override fun formatRating(rating: Double): String = ratingTransform(rating)

    override fun formatDiscount(discountPercentage: Double): String =
        discountTransform(discountPercentage)
}
