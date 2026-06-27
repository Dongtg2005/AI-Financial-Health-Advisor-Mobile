package com.example.mobile.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Nâng cấp độ bo góc để mang lại cảm giác cao cấp (Premium)
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small      = RoundedCornerShape(12.dp),
    medium     = RoundedCornerShape(28.dp),  // Card mặc định (Tăng lên 28)
    large      = RoundedCornerShape(36.dp),  // Hero card, sheets (Tăng lên 36)
    extraLarge = RoundedCornerShape(40.dp)
)
