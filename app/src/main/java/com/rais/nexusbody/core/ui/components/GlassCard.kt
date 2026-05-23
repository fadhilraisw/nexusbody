package com.rais.nexusbody.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerradius: Dp = 32.dp,
    // densitas tinggi (transparansi hanya 3-5%) untuk efek blur 100%
    backgroundcolor: Color = Color(0xFF15171C).copy(alpha = 0.96f),
    bordercolor: Color = Color.White.copy(alpha = 0.08f),
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerradius)

    Box(
        modifier = modifier
            .shadow(elevation = 12.dp, shape = shape, spotColor = Color.Black.copy(alpha = 0.5f))
            .clip(shape)
            .background(backgroundcolor)
            .border(width = 0.5.dp, color = bordercolor, shape = shape)
    ) {
        content()
    }
}