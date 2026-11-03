package com.example.mludo.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mludo.model.PlayerColor
import com.example.mludo.ui.theme.FredokaFontFamily

@Composable
fun MapPinTokenView(
    color: PlayerColor,
    isMovable: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    stackCount: Int = 1,
    sizeDp: Dp = 26.dp
) {
    val bounceAnim = remember { Animatable(0f) }

    LaunchedEffect(isMovable) {
        if (isMovable) {
            bounceAnim.animateTo(
                targetValue = -8f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 400, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            bounceAnim.snapTo(0f)
        }
    }

    val pinColor = color.primaryColor
    val darkColor = color.darkColor
    val lightColor = color.secondaryColor

    Box(
        modifier = modifier
            .size(sizeDp * 1.3f)
            .offset(y = bounceAnim.value.dp)
            .testTag("token_${color.name}")
            .clickable(enabled = isMovable) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(sizeDp * 1.3f)) {
            val w = size.width
            val h = size.height
            val cx = w / 2f

            // Shadow at base
            drawOval(
                color = Color(0x66000000),
                topLeft = Offset(cx - w * 0.3f, h * 0.75f),
                size = Size(w * 0.6f, h * 0.2f)
            )

            // Glowing halo ring if movable
            if (isMovable) {
                drawCircle(
                    color = Color.Yellow.copy(alpha = 0.8f),
                    radius = w * 0.48f,
                    center = Offset(cx, h * 0.45f),
                    style = Stroke(width = 4f)
                )
            }

            // Map pin shape: Pin Body (tapered downward)
            val path = Path().apply {
                moveTo(cx, h * 0.85f) // Tip bottom
                cubicTo(
                    cx - w * 0.15f, h * 0.65f,
                    cx - w * 0.38f, h * 0.5f,
                    cx - w * 0.38f, h * 0.38f
                )
                arcToRad(
                    rect = androidx.compose.ui.geometry.Rect(
                        left = cx - w * 0.38f,
                        top = h * 0.05f,
                        right = cx + w * 0.38f,
                        bottom = h * 0.71f
                    ),
                    startAngleRadians = Math.PI.toFloat(),
                    sweepAngleRadians = Math.PI.toFloat(),
                    forceMoveTo = false
                )
                cubicTo(
                    cx + w * 0.38f, h * 0.5f,
                    cx + w * 0.15f, h * 0.65f,
                    cx, h * 0.85f
                )
                close()
            }

            // Silver/white body gradient with pin head color
            drawPath(
                path = path,
                brush = Brush.radialGradient(
                    colors = listOf(lightColor, pinColor, darkColor),
                    center = Offset(cx - w * 0.1f, h * 0.25f),
                    radius = w * 0.45f
                )
            )

            // Outer border line
            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.8f),
                style = Stroke(width = 2.5f)
            )

            // Inner circle dot inside map pin head
            drawCircle(
                color = Color.White,
                radius = w * 0.14f,
                center = Offset(cx, h * 0.35f)
            )
            drawCircle(
                color = pinColor,
                radius = w * 0.09f,
                center = Offset(cx, h * 0.35f)
            )
        }

        // Stack count badge if multiple tokens on same square
        if (stackCount > 1) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(16.dp)
                    .clip(CircleShape)
                    .clickable { if (isMovable) onClick() },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(color = Color.Black)
                    drawCircle(color = Color.Yellow, style = Stroke(width = 2f))
                }
                Text(
                    text = "$stackCount",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontFamily = FredokaFontFamily,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
