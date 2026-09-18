package com.landoulsi.catalog.shared.presentation.common

/**
 * Fixed to "$" for this demo, since DummyJSON only ever returns USD prices
 * and there is no user- or account-level currency preference to read.
 *
 * In a real app this would resolve the symbol dynamically — e.g. from the
 * signed-in user's billing region, a per-market API response field, or a
 * user-selected display currency — rather than being hardcoded, so
 * [ProductFormatter] depends on this interface instead of a literal "$" to
 * keep that swap a single implementation change.
 */
class CurrencyProvider(val symbol: String = "$")
