package com.example.mludo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.mludo.model.GameState
import com.example.mludo.model.PlayerState
import com.example.mludo.ui.components.BackgroundPattern
import com.example.mludo.ui.components.GlossyButton
import com.example.mludo.ui.components.GlossyButtonStyle
import com.example.mludo.ui.components.MapPinTokenView
import com.example.mludo.ui.theme.FredokaFontFamily

@Composable
fun WinnerScreen(
    gameState: GameState,
    onPlayAgain: () -> Unit,
    onHomeClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sortedPlayers = gameState.players.sortedWith(
        compareBy<PlayerState> { if (it.rank > 0) it.rank else 99 }
    )
    val winner = sortedPlayers.firstOrNull()

    BackgroundPattern(modifier = modifier) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .shadow(16.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0A2B70),
                                Color(0xFF061A47),
                                Color(0xFF04102C)
                            )
                        )
                    )
                    .border(2.5.dp, Color(0xFFFFD700), RoundedCornerShape(24.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Trophy Crown Banner
                Text(
                    text = "🏆 MATCH OVER 🏆",
                    color = Color(0xFFFFD700),
                    fontFamily = FredokaFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (winner != null) {
                    Text(
                        text = "${winner.name} Wins!",
                        color = Color.White,
                        fontFamily = FredokaFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Rankings List
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    sortedPlayers.forEachIndexed { index, player ->
                        val rankLabel = when (index) {
                            0 -> "🥇 1st Place"
                            1 -> "🥈 2nd Place"
                            2 -> "🥉 3rd Place"
                            else -> "4th Place"
                        }

                        val rankBg = when (index) {
                            0 -> Color(0xFF856400)
                            1 -> Color(0xFF424242)
                            2 -> Color(0xFF5D4037)
                            else -> Color(0xFF1B2A4A)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(rankBg)
                                .border(1.5.dp, Color(0xFFFFD700), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                MapPinTokenView(
                                    color = player.color,
                                    isMovable = false,
                                    onClick = {},
                                    sizeDp = 22.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = player.name,
                                    color = Color.White,
                                    fontFamily = FredokaFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Text(
                                text = rankLabel,
                                color = Color(0xFFFFD700),
                                fontFamily = FredokaFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GlossyButton(
                        text = "Play Again",
                        onClick = onPlayAgain,
                        style = GlossyButtonStyle.GOLD,
                        modifier = Modifier.weight(1f),
                        testTag = "play_again_button"
                    )

                    GlossyButton(
                        text = "Home",
                        onClick = onHomeClicked,
                        style = GlossyButtonStyle.BLUE,
                        modifier = Modifier.weight(1f),
                        testTag = "winner_home_button"
                    )
                }
            }
        }
    }
}
