package com.example.mludo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mludo.audio.SoundManager
import com.example.mludo.ui.theme.FredokaFontFamily

enum class GlossyButtonStyle {
    GOLD,
    BLUE,
    RED
}

@Composable
fun GlossyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subText: String? = null,
    style: GlossyButtonStyle = GlossyButtonStyle.GOLD,
    enabled: Boolean = true,
    padding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
    testTag: String = "glossy_button"
) {
    val shape = RoundedCornerShape(28.dp)

    val topColor = when (style) {
        GlossyButtonStyle.GOLD -> Color(0xFFFFF078)
        GlossyButtonStyle.BLUE -> Color(0xFF38B6FF)
        GlossyButtonStyle.RED -> Color(0xFFFF6B6B)
    }

    val midColor = when (style) {
        GlossyButtonStyle.GOLD -> Color(0xFFFFB800)
        GlossyButtonStyle.BLUE -> Color(0xFF0072FF)
        GlossyButtonStyle.RED -> Color(0xFFE50000)
    }

    val bottomColor = when (style) {
        GlossyButtonStyle.GOLD -> Color(0xFFC78200)
        GlossyButtonStyle.BLUE -> Color(0xFF0044B3)
        GlossyButtonStyle.RED -> Color(0xFF990000)
    }

    val borderColor = when (style) {
        GlossyButtonStyle.GOLD -> Color(0xFFFFF5A3)
        GlossyButtonStyle.BLUE -> Color(0xFFBBE2FF)
        GlossyButtonStyle.RED -> Color(0xFFFFC1C1)
    }

    val textColor = when (style) {
        GlossyButtonStyle.GOLD -> Color(0xFF071330)
        GlossyButtonStyle.BLUE -> Color.White
        GlossyButtonStyle.RED -> Color.White
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .testTag(testTag)
            .shadow(
                elevation = if (enabled) 8.dp else 2.dp,
                shape = shape,
                clip = false
            )
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = if (enabled) listOf(topColor, midColor, bottomColor)
                    else listOf(Color.Gray, Color.DarkGray)
                )
            )
            .border(
                width = 2.5.dp,
                brush = Brush.verticalGradient(listOf(borderColor, borderColor.copy(alpha = 0.5f))),
                shape = shape
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null
            ) {
                SoundManager.playButtonClick()
                onClick()
            }
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = text,
                fontFamily = FredokaFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = textColor
            )
            if (!subText.isNullOrEmpty()) {
                Text(
                    text = subText,
                    fontFamily = FredokaFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = textColor.copy(alpha = 0.9f)
                )
            }
        }
    }
}
