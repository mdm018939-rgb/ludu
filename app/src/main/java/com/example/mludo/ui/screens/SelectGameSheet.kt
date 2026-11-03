package com.example.mludo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mludo.model.GameMode
import com.example.mludo.ui.components.BackgroundPattern
import com.example.mludo.ui.components.GlossyButton
import com.example.mludo.ui.components.GlossyButtonStyle
import com.example.mludo.ui.theme.FredokaFontFamily

@Composable
fun SelectGameSheet(
    initialMode: GameMode = GameMode.CLASSIC,
    onNextClicked: (GameMode) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMode by remember { mutableStateOf(initialMode) }

    BackgroundPattern(modifier = modifier) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Dark blue bottom sheet card matching screenshot
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize(0.85f)
                    .shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0A2B70),
                                Color(0xFF061A47),
                                Color(0xFF04102C)
                            )
                        )
                    )
                    .border(
                        width = 2.dp,
                        color = Color(0xFFFFD700),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0564B5))
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SELECT GAME",
                        color = Color(0xFFFFD700),
                        fontFamily = FredokaFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Mode Option Cards
                // 1. Classic Mode (Default)
                GameModeCard(
                    title = "Classic",
                    description = "Standard rules, bring 4 tokens home",
                    isSelected = (selectedMode == GameMode.CLASSIC),
                    onClick = { selectedMode = GameMode.CLASSIC },
                    testTag = "mode_classic_card"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Quick Mode
                GameModeCard(
                    title = "Quick",
                    description = "Faster gameplay, first token home wins",
                    isSelected = (selectedMode == GameMode.QUICK),
                    onClick = { selectedMode = GameMode.QUICK },
                    testTag = "mode_quick_card"
                )

                Spacer(modifier = Modifier.weight(1f))

                // Action Bar: Back arrow bottom-left, Next button center
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Back button bottom-left
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(48.dp)
                            .shadow(6.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFFFFE052), Color(0xFFE5A100))
                                )
                            )
                            .border(2.dp, Color(0xFF071330), CircleShape)
                            .clickable { onBackClicked() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF071330),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Next button
                    GlossyButton(
                        text = "Next",
                        onClick = { onNextClicked(selectedMode) },
                        style = GlossyButtonStyle.GOLD,
                        modifier = Modifier.width(180.dp),
                        testTag = "select_game_next_button"
                    )
                }
            }
        }
    }
}

@Composable
private fun GameModeCard(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val borderColor = if (isSelected) Color(0xFFFFD700) else Color(0x66FFFFFF)
    val cardBackground = if (isSelected) Color(0xFF0052B3) else Color(0xFF081C45)

    Row(
        modifier = Modifier
            .testTag(testTag)
            .fillMaxWidth()
            .shadow(if (isSelected) 8.dp else 2.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(cardBackground)
            .border(2.5.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Selection Checkmark Circle
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color(0xFF00C853) else Color.Transparent)
                .border(2.dp, if (isSelected) Color.White else Color.Gray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                color = Color.White,
                fontFamily = FredokaFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = description,
                color = Color.White.copy(alpha = 0.7f),
                fontFamily = FredokaFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp
            )
        }
    }
}
