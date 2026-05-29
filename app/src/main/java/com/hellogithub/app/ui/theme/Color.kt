package com.hellogithub.app.ui.theme

import androidx.compose.ui.graphics.Color

// ── Light Theme: Black & White Minimalist ──
val PrimaryLight = Color(0xFF1A1A1A)          // near-black for accents
val OnPrimaryLight = Color(0xFFFFFFFF)         // white on dark
val SecondaryLight = Color(0xFF666666)         // medium gray
val OnSecondaryLight = Color(0xFFFFFFFF)
val BackgroundLight = Color(0xFFFAFAFA)        // almost-white bg
val OnBackgroundLight = Color(0xFF1A1A1A)      // near-black text
val SurfaceLight = Color(0xFFFFFFFF)           // pure white cards
val OnSurfaceLight = Color(0xFF1A1A1A)
val SurfaceVariantLight = Color(0xFFF5F5F5)    // subtle gray surfaces
val OnSurfaceVariantLight = Color(0xFF888888)  // secondary text
val OutlineLight = Color(0xFFE5E5E5)           // hairline border
val ErrorLight = Color(0xFFDC2626)

// ── Dark Theme: Black & White Inverted ──
val PrimaryDark = Color(0xFFFFFFFF)            // white accents
val OnPrimaryDark = Color(0xFF1A1A1A)
val SecondaryDark = Color(0xFFAAAAAA)
val OnSecondaryDark = Color(0xFF1A1A1A)
val BackgroundDark = Color(0xFF111111)         // deep black bg
val OnBackgroundDark = Color(0xFFEEEEEE)
val SurfaceDark = Color(0xFF1C1C1C)            // dark card
val OnSurfaceDark = Color(0xFFEEEEEE)
val SurfaceVariantDark = Color(0xFF2A2A2A)
val OnSurfaceVariantDark = Color(0xFF999999)
val OutlineDark = Color(0xFF333333)
val ErrorDark = Color(0xFFEF4444)

// ── Language colors (GitHub linguist) ──
val LangColors = mapOf(
    "Python" to Color(0xFF3572A5),
    "Java" to Color(0xFFB07219),
    "JavaScript" to Color(0xFFF7DF1E),
    "TypeScript" to Color(0xFF3178C6),
    "Go" to Color(0xFF00ADD8),
    "Rust" to Color(0xFFDEA584),
    "C++" to Color(0xFFF34B7D),
    "C" to Color(0xFF555555),
    "Kotlin" to Color(0xFFA97BFF),
    "Swift" to Color(0xFFF05138),
    "Ruby" to Color(0xFF701516),
    "PHP" to Color(0xFF4F5D95),
)
