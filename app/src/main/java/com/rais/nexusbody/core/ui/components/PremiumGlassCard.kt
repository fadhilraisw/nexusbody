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
import androidx.compose.ui.unit.dp

@Composable
fun PremiumGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(28.dp), spotColor = Color.Black.copy(0.5f))
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFF15171C).copy(alpha = 0.96f))
            .border(0.5.dp, Color.White.copy(0.08f), RoundedCornerShape(28.dp))
    ) {
        content()
    }
}