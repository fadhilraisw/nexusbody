package com.rais.nexusbody.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NeonProgressBar(
    label: String,
    level: Int,
    progress: Float,            // 0f to 1f
    color: Color,
    modifier: Modifier = Modifier,
    height: Dp = 10.dp
) {
    var targetProgress by remember { mutableStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 900),
        label = "muscle_xp_progress"
    )

    LaunchedEffect(progress) { targetProgress = progress }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Lv.",
                    color = color.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
                Text(
                    text = "$level",
                    color = color,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.08f))
                .border(
                    width = 0.5.dp,
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(50)
                )
        ) {
            // Glow blur layer behind
            if (animatedProgress > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(color.copy(alpha = 0.4f))
                        .blur(6.dp)
                )
            }
            // Actual progress fill
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                color.copy(alpha = 0.8f),
                                color,
                                color.copy(alpha = 0.9f)
                            )
                        )
                    )
            )
        }
    }
}