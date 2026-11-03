package com.example.mludo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp

@Composable
fun BackgroundPattern(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF061028),
                        Color(0xFF09193E),
                        Color(0xFF0D255C)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val patternSize = 140.dp.toPx()
            val rows = (size.height / patternSize).toInt() + 2
            val cols = (size.width / patternSize).toInt() + 2

            val subtleColor = Color(0x0EFFFFFF)

            for (r in -1..rows) {
                for (c in -1..cols) {
                    val x = c * patternSize + (if (r % 2 == 0) patternSize / 2 else 0f)
                    val y = r * patternSize

                    translate(left = x, top = y) {
                        rotate(degrees = 25f, pivot = Offset(30f, 30f)) {
                            // Draw mini board outline
                            drawRect(
                                color = subtleColor,
                                topLeft = Offset(0f, 0f),
                                size = Size(60f, 60f),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                            )
                            // Mini board cross lines
                            drawLine(subtleColor, Offset(30f, 0f), Offset(30f, 60f), strokeWidth = 1.5f)
                            drawLine(subtleColor, Offset(0f, 30f), Offset(60f, 30f), strokeWidth = 1.5f)

                            // Mini dice next to it
                            drawRoundRect(
                                color = subtleColor,
                                topLeft = Offset(75f, 10f),
                                size = Size(35f, 35f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                            )
                            // Dice pips
                            drawCircle(subtleColor, radius = 3f, center = Offset(83f, 18f))
                            drawCircle(subtleColor, radius = 3f, center = Offset(102f, 37f))
                            drawCircle(subtleColor, radius = 3f, center = Offset(92.5f, 27.5f))
                        }
                    }
                }
            }
        }
        content()
    }
}
