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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mludo.model.PairOption
import com.example.mludo.model.PlayerColor
import com.example.mludo.model.PlayerCountOption
import com.example.mludo.ui.components.BackgroundPattern
import com.example.mludo.ui.components.GlossyButton
import com.example.mludo.ui.components.GlossyButtonStyle
import com.example.mludo.ui.components.MapPinTokenView
import com.example.mludo.ui.theme.FredokaFontFamily

@Composable
fun ChooseColorSheet(
    onStartGame: (
        playerCount: PlayerCountOption,
        pairOption: PairOption,
        playerNames: Map<PlayerColor, String>,
        oneTokenOut: Boolean
    ) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var playerCount by remember { mutableStateOf(PlayerCountOption.P2) }
    var selectedPair by remember { mutableStateOf(PairOption.RED_YELLOW) }

    val namesMap = remember {
        mutableStateMapOf(
            PlayerColor.RED to "Player 1",
            PlayerColor.YELLOW to "Player 2",
            PlayerColor.GREEN to "Player 3",
            PlayerColor.BLUE to "Player 4"
        )
    }

    BackgroundPattern(modifier = modifier) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize(0.92f)
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
                    .padding(16.dp),
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
                        text = "CHOOSE COLOR AND NAME",
                        color = Color(0xFFFFD700),
                        fontFamily = FredokaFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 2P / 3P / 4P / 5P / 6P Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        PlayerCountOption.values().forEach { option ->
                            val isSelected = (playerCount == option)
                            val isDisabled = (option == PlayerCountOption.P5 || option == PlayerCountOption.P6)

                            Box(
                                modifier = Modifier
                                    .size(width = 54.dp, height = 40.dp)
                                    .shadow(if (isSelected) 6.dp else 2.dp, RoundedCornerShape(12.dp))
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        when {
                                            isDisabled -> Color(0xFF1C2B4D)
                                            isSelected -> Color(0xFF0072FF)
                                            else -> Color(0xFF061538)
                                        }
                                    )
                                    .border(
                                        width = 2.dp,
                                        color = if (isSelected) Color(0xFFFFD700) else Color(0x44FFFFFF),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable(enabled = !isDisabled) { playerCount = option },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = option.name,
                                    color = if (isDisabled) Color.Gray else if (isSelected) Color(0xFFFFD700) else Color.White,
                                    fontFamily = FredokaFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Player Configurations
                    if (playerCount == PlayerCountOption.P2) {
                        // Pair A: Green + Blue (Row A)
                        PairOptionCard(
                            pairName = "Row A",
                            colors = listOf(PlayerColor.GREEN, PlayerColor.BLUE),
                            isSelected = (selectedPair == PairOption.GREEN_BLUE),
                            onSelect = { selectedPair = PairOption.GREEN_BLUE },
                            namesMap = namesMap
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Pair B: Red + Yellow (Row B - Default Selected)
                        PairOptionCard(
                            pairName = "Row B",
                            colors = listOf(PlayerColor.RED, PlayerColor.YELLOW),
                            isSelected = (selectedPair == PairOption.RED_YELLOW),
                            onSelect = { selectedPair = PairOption.RED_YELLOW },
                            namesMap = namesMap
                        )
                    } else {
                        // 3P or 4P: One row per player
                        val activeColors = when (playerCount) {
                            PlayerCountOption.P3 -> listOf(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.YELLOW)
                            else -> listOf(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.YELLOW, PlayerColor.BLUE)
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            activeColors.forEachIndexed { index, color ->
                                PlayerNameRow(
                                    color = color,
                                    name = namesMap[color] ?: "Player ${index + 1}",
                                    onNameChanged = { namesMap[color] = it }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Play Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlossyButton(
                        text = "Play",
                        subText = "Need a 6 to bring token out",
                        onClick = {
                            onStartGame(playerCount, selectedPair, namesMap, false)
                        },
                        style = GlossyButtonStyle.GOLD,
                        modifier = Modifier.fillMaxWidth(0.85f),
                        testTag = "play_standard_button"
                    )

                    GlossyButton(
                        text = "Play",
                        subText = "- One Token Out -",
                        onClick = {
                            onStartGame(playerCount, selectedPair, namesMap, true)
                        },
                        style = GlossyButtonStyle.GOLD,
                        modifier = Modifier.fillMaxWidth(0.85f),
                        testTag = "play_one_token_out_button"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Back Button Bottom Left
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
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
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PairOptionCard(
    pairName: String,
    colors: List<PlayerColor>,
    isSelected: Boolean,
    onSelect: () -> Unit,
    namesMap: MutableMap<PlayerColor, String>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (isSelected) 8.dp else 2.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) Color(0xFF0052B3) else Color(0xFF081C45))
            .border(2.5.dp, if (isSelected) Color(0xFFFFD700) else Color(0x44FFFFFF), RoundedCornerShape(16.dp))
            .clickable { onSelect() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Selection Checkmark Circle
        Box(
            modifier = Modifier
                .size(30.dp)
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
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            colors.forEachIndexed { index, color ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    MapPinTokenView(
                        color = color,
                        isMovable = false,
                        onClick = {},
                        sizeDp = 20.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = namesMap[color] ?: "Player ${index + 1}",
                        onValueChange = { namesMap[color] = it },
                        textStyle = TextStyle(
                            fontFamily = FredokaFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFD700),
                            unfocusedBorderColor = Color(0x66FFFFFF),
                            focusedContainerColor = Color(0xFF05173D),
                            unfocusedContainerColor = Color(0xFF05173D)
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerNameRow(
    color: PlayerColor,
    name: String,
    onNameChanged: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF081C45))
            .border(1.5.dp, Color(0x44FFFFFF), RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MapPinTokenView(
            color = color,
            isMovable = false,
            onClick = {},
            sizeDp = 22.dp
        )
        Spacer(modifier = Modifier.width(12.dp))
        OutlinedTextField(
            value = name,
            onValueChange = onNameChanged,
            textStyle = TextStyle(
                fontFamily = FredokaFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 15.sp
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFFD700),
                unfocusedBorderColor = Color(0x66FFFFFF),
                focusedContainerColor = Color(0xFF05173D),
                unfocusedContainerColor = Color(0xFF05173D)
            ),
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
        )
    }
}
