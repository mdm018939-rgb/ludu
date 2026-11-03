package com.example.mludo.model

import androidx.compose.ui.graphics.Color

enum class PlayerColor(
    val displayName: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val darkColor: Color
) {
    RED("Red", Color(0xFFE53935), Color(0xFFFF8A80), Color(0xFFB71C1C)),
    GREEN("Green", Color(0xFF43A047), Color(0xFFA5D6A7), Color(0xFF1B5E20)),
    YELLOW("Yellow", Color(0xFFFDD835), Color(0xFFFFF59D), Color(0xFFF57F17)),
    BLUE("Blue", Color(0xFF1E88E5), Color(0xFF90CAF9), Color(0xFF0D47A1))
}

enum class GameMode {
    CLASSIC,
    QUICK
}

enum class PlayerCountOption {
    P2, P3, P4, P5, P6
}

enum class PairOption {
    RED_YELLOW,  // Row B (Default selected)
    GREEN_BLUE   // Row A
}

enum class TokenLocation {
    YARD,
    PATH,
    HOME
}

data class TokenState(
    val id: Int, // 0..3
    val color: PlayerColor,
    val stepPosition: Int = -1, // -1 if in YARD, 0..50 on common track, 51..55 in home path, 56 is HOME
    val location: TokenLocation = TokenLocation.YARD
)

data class PlayerState(
    val color: PlayerColor,
    val name: String,
    val tokens: List<TokenState> = List(4) { id -> TokenState(id = id, color = color) },
    val rank: Int = 0, // 1 for 1st, 2 for 2nd, etc. 0 if not finished
    val isFinished: Boolean = false
)

enum class DiceRollState {
    CAN_ROLL,
    ROLLING,
    CAN_MOVE,
    NO_MOVES,
    THREE_SIXES
}

data class BoardCell(
    val row: Int,
    val col: Int
)

data class GameState(
    val gameId: String = System.currentTimeMillis().toString(),
    val mode: GameMode = GameMode.CLASSIC,
    val playerCount: PlayerCountOption = PlayerCountOption.P2,
    val pairOption: PairOption = PairOption.RED_YELLOW,
    val oneTokenOut: Boolean = false,
    val players: List<PlayerState> = emptyList(),
    val activePlayerIndex: Int = 0,
    val diceValue: Int = 1,
    val diceState: DiceRollState = DiceRollState.CAN_ROLL,
    val consecutiveSixes: Int = 0,
    val isPaused: Boolean = false,
    val winnerRanks: List<PlayerColor> = emptyList(),
    val isGameOver: Boolean = false,
    val debugMode: Boolean = false,
    val soundEnabled: Boolean = true
)
