package com.palaksinghal.mysaarthi.presentation.theme


import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.palaksinghal.mysaarthi.R


val CaprasimoFamily= FontFamily(Font(R.font.caprasimo_regular, FontWeight.Normal))

val FigtreeFamily =FontFamily(Font(R.font.figtree_regular, FontWeight.Normal),
    Font(R.font.figtree_bold, FontWeight.Bold),
    Font(R.font.figtree_semibold, FontWeight.SemiBold),
    Font(R.font.figtree_medium, FontWeight.Medium)
    )

// Set of Material typography styles to start with
val MySaarthiTypography = Typography(

    // Display & headline — Caprasimo, for big moments (screen titles, shloka display)
    displayLarge = TextStyle(fontFamily = CaprasimoFamily, fontWeight = FontWeight.Normal, fontSize = 36.sp, lineHeight = 44.sp),
    displayMedium = TextStyle(fontFamily = CaprasimoFamily, fontWeight = FontWeight.Normal, fontSize = 30.sp, lineHeight = 38.sp),
    headlineLarge = TextStyle(fontFamily = CaprasimoFamily, fontWeight = FontWeight.Normal, fontSize = 26.sp, lineHeight = 34.sp),
    headlineMedium = TextStyle(fontFamily = CaprasimoFamily, fontWeight = FontWeight.Normal, fontSize = 22.sp, lineHeight = 30.sp),

    // Titles — Figtree SemiBold, for card headers, section titles
    titleLarge = TextStyle(fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 24.sp),
    titleSmall = TextStyle(fontFamily = FigtreeFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),

    // Body — Figtree Regular, for everything readable
    bodyLarge = TextStyle(fontFamily = FigtreeFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = FigtreeFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = FigtreeFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),

    // Labels — Figtree Medium, for buttons, chips, nav labels
    labelLarge = TextStyle(fontFamily = FigtreeFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = FigtreeFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontFamily = FigtreeFamily, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp)
)