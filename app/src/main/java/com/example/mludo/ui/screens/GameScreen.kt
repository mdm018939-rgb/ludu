package com.example.mludo.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mludo.audio.SoundManager
import com.example.mludo.logic.LudoBoardLogic
import com.example.mludo.logic.LudoStorage
import com.example.mludo.model.DiceRollState
import com.example.mludo.model.GameState
import com.example.mludo.model.PlayerColor
import com.example.mludo.ui.components.BackgroundPattern
import com.example.mludo.ui.components.LudoBoardView
import com.example.mludo.ui.components.PlayerDicePanel
import com.example.mludo.ui.theme.FredokaFontFamily
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    initialState: GameState,
    onGameOver: (GameState) -> Unit,
    onExitHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var gameState by remember { mutableStateOf(initialState) }
    var legalTokenIds by remember { mutableStateOf<List<Int>>(emptyList()) }
    var statusBannerText by remember { mutableStateOf<String?>(null) }
    var forcedDiceValue by remember { mutableIntStateOf(0) }

    // Save game locally whenever state changes
    LaunchedEffect(gameState) {
        LudoStorage.saveGame(context, gameState)
        if (gameState.isGameOver) {
            SoundManager.playWinSound()
            onGameOver(gameState)
        }
    }

    // Auto turn handling and move validation
    fun rollDice() {
        if (gameState.diceState != DiceRollState.CAN_ROLL) return

        val rolled = if (forcedDiceValue > 0) forcedDiceValue else (1..6).random()
        val isThreeSixes = (rolled == 6 && gameState.consecutiveSixes >= 2)

        if (isThreeSixes) {
            // Three 6s penalty! Lost turn
            val updatedConsecutive = 0
            var nextIdx = (gameState.activePlayerIndex + 1) % gameState.players.size
            while (gameState.players[nextIdx].isFinished) {
                nextIdx = (nextIdx + 1) % gameState.players.size
            }

            gameState = gameState.copy(
                diceValue = rolled,
                diceState = DiceRollState.THREE_SIXES,
                consecutiveSixes = updatedConsecutive
            )
            statusBannerText = "Three 6s in a row! Turn lost."
        } else {
            val consecutive = if (rolled == 6) gameState.consecutiveSixes + 1 else 0
            gameState = gameState.copy(
                diceValue = rolled,
                diceState = DiceRollState.ROLLING,
                consecutiveSixes = consecutive
            )
        }
    }

    // When rolling animation finishes, compute legal moves
    LaunchedEffect(gameState.diceState) {
        if (gameState.diceState == DiceRollState.ROLLING) {
            delay(500) // allow rolling animation time
            val legal = LudoBoardLogic.getLegalMoves(gameState, gameState.diceValue)
            legalTokenIds = legal

            if (legal.isEmpty()) {
                gameState = gameState.copy(diceState = DiceRollState.NO_MOVES)
                statusBannerText = "No legal moves available"
                delay(1000)
                statusBannerText = null

                // Pass turn to next active player
                var nextIdx = (gameState.activePlayerIndex + 1) % gameState.players.size
                while (gameState.players[nextIdx].isFinished) {
                    nextIdx = (nextIdx + 1) % gameState.players.size
                }
                gameState = gameState.copy(
                    activePlayerIndex = nextIdx,
                    diceState = DiceRollState.CAN_ROLL,
                    consecutiveSixes = 0
                )
            } else if (legal.size == 1) {
                // Auto-move single legal token for smooth gameplay!
                gameState = gameState.copy(diceState = DiceRollState.CAN_MOVE)
                delay(300)
                val movedState = LudoBoardLogic.executeMove(gameState, legal.first(), gameState.diceValue)
                SoundManager.playStepSound()
                legalTokenIds = emptyList()
                gameState = movedState
            } else {
                gameState = gameState.copy(diceState = DiceRollState.CAN_MOVE)
            }
        } else if (gameState.diceState == DiceRollState.THREE_SIXES) {
            delay(1200)
            statusBannerText = null
            var nextIdx = (gameState.activePlayerIndex + 1) % gameState.players.size
            while (gameState.players[nextIdx].isFinished) {
                nextIdx = (nextIdx + 1) % gameState.players.size
            }
            gameState = gameState.copy(
                activePlayerIndex = nextIdx,
                diceState = DiceRollState.CAN_ROLL,
                consecutiveSixes = 0
            )
        }
    }

    // Handle user tapping a token
    fun onTokenClicked(tokenId: Int) {
        if (gameState.diceState != DiceRollState.CAN_MOVE) return
        if (tokenId !in legalTokenIds) return

        val movedState = LudoBoardLogic.executeMove(gameState, tokenId, gameState.diceValue)
        SoundManager.playStepSound()
        legalTokenIds = emptyList()
        gameState = movedState
    }

    BackgroundPattern(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Identify Top player vs Bottom player for Pass-and-Play
            val activePlayer = gameState.players[gameState.activePlayerIndex]
            val topPlayer = gameState.players.find { it.color == PlayerColor.GREEN || it.color == PlayerColor.YELLOW } ?: gameState.players.first()
            val bottomPlayer = gameState.players.find { it.color == PlayerColor.RED || it.color == PlayerColor.BLUE } ?: gameState.players.last()

            // Top Player Dice Panel (Rotated 180)
            PlayerDicePanel(
                color = topPlayer.color,
                playerName = topPlayer.name,
                diceValue = if (activePlayer.color == topPlayer.color) gameState.diceValue else 1,
                diceState = gameState.diceState,
                isActive = (activePlayer.color == topPlayer.color),
                isRotated = true,
                onRollDice = { rollDice() }
            )

            // Center Board Area (Portrait full width)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                LudoBoardView(
                    gameState = gameState,
                    legalTokenIds = legalTokenIds,
                    onTokenClicked = { onTokenClicked(it) }
                )

                // Status Banner Toast overlay
                if (!statusBannerText.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xEE071330))
                            .border(2.dp, Color(0xFFFFD700), RoundedCornerShape(16.dp))
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = statusBannerText ?: "",
                            color = Color(0xFFFFD700),
                            fontFamily = FredokaFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            // Bottom Area: Menu Button bottom-left, Bottom Player Dice Panel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                // Pause Menu Button Bottom-Left
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(46.dp)
                        .shadow(6.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xFF07193F))
                        .border(2.dp, Color(0xFFFFD700), CircleShape)
                        .testTag("pause_menu_button")
                        .clickable { gameState = gameState.copy(isPaused = true) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Pause Menu",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Debug Dice Override selector (if debug mode enabled in pause menu)
                if (gameState.debugMode) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xCC000000))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        (1..6).forEach { num ->
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(if (forcedDiceValue == num) Color(0xFFFFD700) else Color.DarkGray)
                                    .clickable {
                                        forcedDiceValue = if (forcedDiceValue == num) 0 else num
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$num",
                                    color = if (forcedDiceValue == num) Color.Black else Color.White,
                                    fontSize = 12.sp,
                                    fontFamily = FredokaFontFamily,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Bottom Player Dice Panel
                PlayerDicePanel(
                    color = bottomPlayer.color,
                    playerName = bottomPlayer.name,
                    diceValue = if (activePlayer.color == bottomPlayer.color) gameState.diceValue else 1,
                    diceState = gameState.diceState,
                    isActive = (activePlayer.color == bottomPlayer.color),
                    isRotated = false,
                    onRollDice = { rollDice() },
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }

    // Pause Dialog
    if (gameState.isPaused) {
        PauseMenuDialog(
            soundEnabled = gameState.soundEnabled,
            debugMode = gameState.debugMode,
            onToggleSound = { enabled ->
                SoundManager.setMuted(!enabled)
                gameState = gameState.copy(soundEnabled = enabled)
            },
            onToggleDebug = { enabled ->
                gameState = gameState.copy(debugMode = enabled)
            },
            onResume = { gameState = gameState.copy(isPaused = false) },
            onRestart = {
                val resetPlayers = gameState.players.map { p ->
                    val tokens = List(4) { id ->
                        val startStep = if (gameState.oneTokenOut && id == 0) 0 else -1
                        val startLoc = if (startStep == 0) com.example.mludo.model.TokenLocation.PATH else com.example.mludo.model.TokenLocation.YARD
                        com.example.mludo.model.TokenState(id = id, color = p.color, stepPosition = startStep, location = startLoc)
                    }
                    p.copy(tokens = tokens, rank = 0, isFinished = false)
                }
                gameState = gameState.copy(
                    players = resetPlayers,
                    activePlayerIndex = 0,
                    diceValue = 1,
                    diceState = DiceRollState.CAN_ROLL,
                    consecutiveSixes = 0,
                    winnerRanks = emptyList(),
                    isGameOver = false,
                    isPaused = false
                )
            },
            onExitHome = {
                LudoStorage.clearSavedGame(context)
                onExitHome()
            }
        )
    }
}
