package com.landoulsi.catalog.shared.presentation.common

/**
 * Formats numerical values (prices, ratings, discounts) into display strings.
 */
interface ProductFormatter {

    /** Formats a price with a currency symbol prefix and two decimals in the configured locale. */
    fun formatPrice(price: Double): String

    /** Formats a rating to one decimal in the configured locale. */
    fun formatRating(rating: Double): String

    /** Formats a discount percentage with no decimals in the configured locale. */
    fun formatDiscount(discountPercentage: Double): String
}
