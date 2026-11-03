package com.example.MainActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.mludo.logic.LudoStorage
import com.example.mludo.model.GameMode
import com.example.mludo.model.GameState
import com.example.mludo.model.PairOption
import com.example.mludo.model.PlayerColor
import com.example.mludo.model.PlayerCountOption
import com.example.mludo.model.PlayerState
import com.example.mludo.model.TokenLocation
import com.example.mludo.model.TokenState
import com.example.mludo.ui.screens.ChooseColorSheet
import com.example.mludo.ui.screens.GameScreen
import com.example.mludo.ui.screens.HomeScreen
import com.example.mludo.ui.screens.SelectGameSheet
import com.example.mludo.ui.screens.SplashScreen
import com.example.mludo.ui.screens.WinnerScreen
import com.example.ui.theme.MLudoTheme

enum class Screen {
    SPLASH,
    HOME,
    SELECT_GAME,
    CHOOSE_COLOR,
    GAME,
    WINNER
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MLudoTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MLudoApp()
                }
            }
        }
    }
}

@Composable
fun MLudoApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    var currentScreen by remember { mutableStateOf(Screen.SPLASH) }
    var selectedMode by remember { mutableStateOf(GameMode.CLASSIC) }

    var currentGameProps by remember { mutableStateOf<GameState?>(null) }
    val hasSavedGame = remember(currentScreen) { LudoStorage.hasSavedGame(context) }

    fun startNewGame(
        playerCount: PlayerCountOption,
        pairOption: PairOption,
        namesMap: Map<PlayerColor, String>,
        oneTokenOut: Boolean
    ) {
        val selectedColors = when (playerCount) {
            PlayerCountOption.P2 -> when (pairOption) {
                PairOption.RED_YELLOW -> listOf(PlayerColor.RED, PlayerColor.YELLOW)
                PairOption.GREEN_BLUE -> listOf(PlayerColor.GREEN, PlayerColor.BLUE)
            }
            PlayerCountOption.P3 -> listOf(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.YELLOW)
            else -> listOf(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.YELLOW, PlayerColor.BLUE)
        }

        val players = selectedColors.map { color ->
            val tokens = List(4) { id ->
                val isFirstTokenOut = (oneTokenOut && id == 0)
                val startStep = if (isFirstTokenOut) 0 else -1
                val startLoc = if (isFirstTokenOut) TokenLocation.PATH else TokenLocation.YARD
                TokenState(id = id, color = color, stepPosition = startStep, location = startLoc)
            }
            PlayerState(
                color = color,
                name = namesMap[color] ?: color.displayName,
                tokens = tokens
            )
        }

        val state = GameState(
            mode = selectedMode,
            playerCount = playerCount,
            pairOption = pairOption,
            oneTokenOut = oneTokenOut,
            players = players,
            activePlayerIndex = 0
        )

        LudoStorage.saveGame(context, state)
        currentGameProps = state
        currentScreen = Screen.GAME
    }

    when (currentScreen) {
        Screen.SPLASH -> {
            SplashScreen(
                onSplashFinished = { currentScreen = Screen.HOME }
            )
        }

        Screen.HOME -> {
            HomeScreen(
                hasSavedGame = hasSavedGame,
                onPassNPlayClicked = { currentScreen = Screen.SELECT_GAME },
                onResumeGameClicked = {
                    val saved = LudoStorage.loadGame(context)
                    if (saved != null) {
                        currentGameProps = saved
                        currentScreen = Screen.GAME
                    }
                },
                onOpenSettings = { currentScreen = Screen.SELECT_GAME }
            )
        }

        Screen.SELECT_GAME -> {
            BackHandler { currentScreen = Screen.HOME }
            SelectGameSheet(
                initialMode = selectedMode,
                onNextClicked = { mode ->
                    selectedMode = mode
                    currentScreen = Screen.CHOOSE_COLOR
                },
                onBackClicked = { currentScreen = Screen.HOME }
            )
        }

        Screen.CHOOSE_COLOR -> {
            BackHandler { currentScreen = Screen.SELECT_GAME }
            ChooseColorSheet(
                onStartGame = { playerCount, pairOption, namesMap, oneTokenOut ->
                    startNewGame(playerCount, pairOption, namesMap, oneTokenOut)
                },
                onBackClicked = { currentScreen = Screen.SELECT_GAME }
            )
        }

        Screen.GAME -> {
            BackHandler { currentScreen = Screen.HOME }
            val props = currentGameProps ?: LudoStorage.loadGame(context)
            if (props != null) {
                GameScreen(
                    initialState = props,
                    onGameOver = { finalState ->
                        currentGameProps = finalState
                        currentScreen = Screen.WINNER
                    },
                    onExitHome = { currentScreen = Screen.HOME }
                )
            } else {
                currentScreen = Screen.HOME
            }
        }

        Screen.WINNER -> {
            BackHandler { currentScreen = Screen.HOME }
            val props = currentGameProps
            if (props != null) {
                WinnerScreen(
                    gameState = props,
                    onPlayAgain = { currentScreen = Screen.CHOOSE_COLOR },
                    onHomeClicked = { currentScreen = Screen.HOME }
                )
            } else {
                currentScreen = Screen.HOME
            }
        }
    }
}
