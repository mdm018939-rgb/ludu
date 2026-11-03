package com.example.mludo.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mludo.audio.SoundManager
import com.example.mludo.model.DiceRollState
import com.example.mludo.model.PlayerColor
import com.example.mludo.ui.theme.FredokaFontFamily
import kotlinx.coroutines.delay

@Composable
fun PlayerDicePanel(
    color: PlayerColor,
    playerName: String,
    diceValue: Int,
    diceState: DiceRollState,
    isActive: Boolean,
    isRotated: Boolean,
    onRollDice: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotateModifier = if (isRotated) Modifier.rotate(180f) else Modifier

    Row(
        modifier = modifier
            .then(rotateModifier)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Player Name Box with Pin Token
        Box(
            modifier = Modifier
                .shadow(4.dp, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            color.primaryColor.copy(alpha = 0.9f),
                            color.darkColor
                        )
                    )
                )
                .border(2.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MapPinTokenView(
                    color = color,
                    isMovable = false,
                    onClick = {},
                    sizeDp = 18.dp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = playerName,
                    color = Color.White,
                    fontFamily = FredokaFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Yellow active pointer arrow if active
        if (isActive) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .padding(4.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val path = Path().apply {
                        moveTo(0f, size.height * 0.2f)
                        lineTo(size.width * 0.6f, size.height * 0.2f)
                        lineTo(size.width * 0.6f, 0f)
                        lineTo(size.width, size.height * 0.5f)
                        lineTo(size.width * 0.6f, size.height)
                        lineTo(size.width * 0.6f, size.height * 0.8f)
                        lineTo(0f, size.height * 0.8f)
                        close()
                    }
                    drawPath(path, Color(0xFFFFD700))
                    drawPath(path, Color.White, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        // Dice Box
        DiceBox(
            diceValue = diceValue,
            diceState = diceState,
            isActive = isActive,
            onRollDice = onRollDice
        )
    }
}

@Composable
fun DiceBox(
    diceValue: Int,
    diceState: DiceRollState,
    isActive: Boolean,
    onRollDice: () -> Unit,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 54.dp
) {
    var displayValue by remember { mutableIntStateOf(diceValue) }
    var isRollingAnim by remember { androidx.compose.runtime.mutableStateOf(false) }
    val rotationAnim = remember { Animatable(0f) }

    LaunchedEffect(diceState, diceValue) {
        if (diceState == DiceRollState.ROLLING) {
            isRollingAnim = true
            SoundManager.playRollSound()
            repeat(6) {
                displayValue = (1..6).random()
                rotationAnim.animateTo(
                    targetValue = (it + 1) * 60f,
                    animationSpec = tween(durationMillis = 50, easing = FastOutSlowInEasing)
                )
                delay(40)
            }
            displayValue = diceValue
            isRollingAnim = false
        } else {
            displayValue = diceValue
        }
    }

    val canClick = isActive && (diceState == DiceRollState.CAN_ROLL)

    Box(
        modifier = modifier
            .testTag("dice_box")
            .size(sizeDp)
            .shadow(
                elevation = if (isActive) 12.dp else 2.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false
            )
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isActive) listOf(Color.White, Color(0xFFF0F0F0), Color(0xFFD9D9D9))
                    else listOf(Color(0xFFB0B0B0), Color(0xFF888888))
                )
            )
            .border(
                width = if (isActive) 3.dp else 1.5.dp,
                color = if (isActive) Color(0xFFFFD700) else Color.Gray,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = canClick) {
                onRollDice()
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotationAnim.value)
        ) {
            val w = size.width
            val h = size.height
            val r = w * 0.08f // pip radius
            val pipColor = if (isActive) Color(0xFF111111) else Color(0xFF444444)

            // Inner dice edge bevel
            drawRoundRect(
                color = Color.White.copy(alpha = 0.6f),
                topLeft = Offset(4f, 4f),
                size = Size(w - 8f, h - 8f),
                cornerRadius = CornerRadius(8.dp.toPx())
            )

            val c = Offset(w / 2f, h / 2f)
            val tl = Offset(w * 0.28f, h * 0.28f)
            val tr = Offset(w * 0.72f, h * 0.28f)
            val bl = Offset(w * 0.28f, h * 0.72f)
            val br = Offset(w * 0.72f, h * 0.72f)
            val ml = Offset(w * 0.28f, h * 0.5f)
            val mr = Offset(w * 0.72f, h * 0.5f)

            when (displayValue) {
                1 -> {
                    drawCircle(Color.Red, radius = r * 1.3f, center = c)
                }
                2 -> {
                    drawCircle(pipColor, radius = r, center = tl)
                    drawCircle(pipColor, radius = r, center = br)
                }
                3 -> {
                    drawCircle(pipColor, radius = r, center = tl)
                    drawCircle(pipColor, radius = r, center = c)
                    drawCircle(pipColor, radius = r, center = br)
                }
                4 -> {
                    drawCircle(pipColor, radius = r, center = tl)
                    drawCircle(pipColor, radius = r, center = tr)
                    drawCircle(pipColor, radius = r, center = bl)
                    drawCircle(pipColor, radius = r, center = br)
                }
                5 -> {
                    drawCircle(pipColor, radius = r, center = tl)
                    drawCircle(pipColor, radius = r, center = tr)
                    drawCircle(pipColor, radius = r, center = c)
                    drawCircle(pipColor, radius = r, center = bl)
                    drawCircle(pipColor, radius = r, center = br)
                }
                6 -> {
                    drawCircle(pipColor, radius = r, center = tl)
                    drawCircle(pipColor, radius = r, center = tr)
                    drawCircle(pipColor, radius = r, center = ml)
                    drawCircle(pipColor, radius = r, center = mr)
                    drawCircle(pipColor, radius = r, center = bl)
                    drawCircle(pipColor, radius = r, center = br)
                }
            }
        }
    }
}
