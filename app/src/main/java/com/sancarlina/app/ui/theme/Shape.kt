package com.sancarlina.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Stitch shapes — chips 12dp, cards/buttons 24dp, sheets 28dp top
val SancarlinaCardShape = RoundedCornerShape(24.dp)
val SancarlinaChipShape = RoundedCornerShape(12.dp)
val SancarlinaButtonShape = RoundedCornerShape(24.dp)
val SancarlinaSheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
val SancarlinaBottomBarShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = SancarlinaChipShape,
    medium = RoundedCornerShape(16.dp),
    large = SancarlinaCardShape,
    extraLarge = RoundedCornerShape(28.dp)
)
