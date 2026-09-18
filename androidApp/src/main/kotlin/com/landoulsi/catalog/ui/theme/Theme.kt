package com.landoulsi.catalog.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

// A clean, modern light palette designed as a single universal theme.
// Soft off-white / light slate surfaces replace the muddy mid-tone gray,
// providing high contrast, comfortable reading for both light- and dark-mode
// users without harsh glare, and seamless framing for product imagery.
private val CatalogColors = lightColorScheme(
    primary = Color(0xFF1D4ED8),             // Royal Blue (high contrast, accessible)
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),    // Soft sky blue container
    onPrimaryContainer = Color(0xFF1E3A8A),  // Deep blue on sky container
    secondary = Color(0xFF475569),           // Neutral slate
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE2E8F0),
    onSecondaryContainer = Color(0xFF1E293B),
    tertiary = Color(0xFFD97706),            // Amber gold for ratings & active tabs
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFEF3C7),   // Warm amber container
    onTertiaryContainer = Color(0xFF78350F),
    background = Color(0xFFF8FAFC),          // Soothing off-white/slate backdrop (zero glare)
    surface = Color(0xFFF8FAFC),
    onSurface = Color(0xFF0F172A),           // Deep slate ink text (crisp readability)
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF64748B),    // Muted slate for category/secondary text
    surfaceContainer = Color(0xFFF1F5F9),    // Image thumbnail frame
    surfaceContainerLow = Color(0xFFFFFFFF), // Crisp elevated white card
    outline = Color(0xFFCBD5E1),             // Subtle border for search bar & outlines
    outlineVariant = Color(0xFFE2E8F0),
    error = Color(0xFFE11D48),               // Vibrant rose for favorite star & discounts
    onError = Color.White,
    errorContainer = Color(0xFFFFE4E6),
    onErrorContainer = Color(0xFF9F1239),
)

private val CatalogTypography = Typography().run {
    copy(
        headlineSmall = headlineSmall.copy(fontWeight = FontWeight.SemiBold),
        titleLarge = titleLarge.copy(fontWeight = FontWeight.SemiBold),
        titleMedium = titleMedium.copy(fontWeight = FontWeight.SemiBold),
        labelLarge = labelLarge.copy(fontWeight = FontWeight.SemiBold),
    )
}

/** App theme: a single crisp, modern light palette used regardless of system setting. */
@Composable
fun ProductCatalogTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CatalogColors,
        typography = CatalogTypography,
        content = content,
    )
}