package com.example.mludo.logic

import com.example.mludo.model.BoardCell
import com.example.mludo.model.DiceRollState
import com.example.mludo.model.GameState
import com.example.mludo.model.PlayerColor
import com.example.mludo.model.PlayerState
import com.example.mludo.model.TokenLocation
import com.example.mludo.model.TokenState

object LudoBoardLogic {

    // 52 Common Track Coordinates (Row, Col) in clockwise sequence starting from Red Start (13, 6)
    val COMMON_TRACK = listOf(
        BoardCell(13, 6), // 0: Red Start (Star)
        BoardCell(12, 6), // 1
        BoardCell(11, 6), // 2
        BoardCell(10, 6), // 3
        BoardCell(9, 6),  // 4
        BoardCell(8, 5),  // 5
        BoardCell(8, 4),  // 6
        BoardCell(8, 3),  // 7
        BoardCell(8, 2),  // 8: Star
        BoardCell(8, 1),  // 9
        BoardCell(8, 0),  // 10
        BoardCell(7, 0),  // 11
        BoardCell(6, 0),  // 12
        BoardCell(6, 1),  // 13: Green Start (Star)
        BoardCell(6, 2),  // 14
        BoardCell(6, 3),  // 15
        BoardCell(6, 4),  // 16
        BoardCell(6, 5),  // 17
        BoardCell(5, 6),  // 18
        BoardCell(4, 6),  // 19
        BoardCell(3, 6),  // 20
        BoardCell(2, 6),  // 21: Star
        BoardCell(1, 6),  // 22
        BoardCell(0, 6),  // 23
        BoardCell(0, 7),  // 24
        BoardCell(0, 8),  // 25
        BoardCell(1, 8),  // 26: Yellow Start (Star)
        BoardCell(2, 8),  // 27
        BoardCell(3, 8),  // 28
        BoardCell(4, 8),  // 29
        BoardCell(5, 8),  // 30
        BoardCell(6, 9),  // 31
        BoardCell(6, 10), // 32
        BoardCell(6, 11), // 33
        BoardCell(6, 12), // 34: Star
        BoardCell(6, 13), // 35
        BoardCell(6, 14), // 36
        BoardCell(7, 14), // 37
        BoardCell(8, 14), // 38
        BoardCell(8, 13), // 39: Blue Start (Star)
        BoardCell(8, 12), // 40
        BoardCell(8, 11), // 41
        BoardCell(8, 10), // 42
        BoardCell(8, 9),  // 43
        BoardCell(9, 8),  // 44
        BoardCell(10, 8), // 45
        BoardCell(11, 8), // 46
        BoardCell(12, 8), // 47: Star
        BoardCell(13, 8), // 48
        BoardCell(14, 8), // 49
        BoardCell(14, 7), // 50
        BoardCell(14, 6)  // 51
    )

    fun getStartIndex(color: PlayerColor): Int = when (color) {
        PlayerColor.RED -> 0
        PlayerColor.GREEN -> 13
        PlayerColor.YELLOW -> 26
        PlayerColor.BLUE -> 39
    }

    // Home column coordinates for steps 51..55, and Center (step 56)
    fun getHomeColumnCell(color: PlayerColor, stepPosition: Int): BoardCell {
        val index = stepPosition - 51 // 0..4 for home column, 5 for center
        return when (color) {
            PlayerColor.RED -> listOf(
                BoardCell(13, 7), BoardCell(12, 7), BoardCell(11, 7),
                BoardCell(10, 7), BoardCell(9, 7), BoardCell(8, 7)
            )[index]
            PlayerColor.GREEN -> listOf(
                BoardCell(7, 1), BoardCell(7, 2), BoardCell(7, 3),
                BoardCell(7, 4), BoardCell(7, 5), BoardCell(7, 6)
            )[index]
            PlayerColor.YELLOW -> listOf(
                BoardCell(1, 7), BoardCell(2, 7), BoardCell(3, 7),
                BoardCell(4, 7), BoardCell(5, 7), BoardCell(6, 7)
            )[index]
            PlayerColor.BLUE -> listOf(
                BoardCell(7, 13), BoardCell(7, 12), BoardCell(7, 11),
                BoardCell(7, 10), BoardCell(7, 9), BoardCell(7, 8)
            )[index]
        }
    }

    // Yard slot positions (4 per color)
    fun getYardSlotCell(color: PlayerColor, tokenId: Int): BoardCell {
        return when (color) {
            PlayerColor.GREEN -> listOf(BoardCell(1, 1), BoardCell(1, 4), BoardCell(4, 1), BoardCell(4, 4))[tokenId]
            PlayerColor.YELLOW -> listOf(BoardCell(1, 10), BoardCell(1, 13), BoardCell(4, 10), BoardCell(4, 13))[tokenId]
            PlayerColor.RED -> listOf(BoardCell(10, 1), BoardCell(10, 4), BoardCell(13, 1), BoardCell(13, 4))[tokenId]
            PlayerColor.BLUE -> listOf(BoardCell(10, 10), BoardCell(10, 13), BoardCell(13, 10), BoardCell(13, 13))[tokenId]
        }
    }

    // Returns exact BoardCell for a token
    fun getTokenCell(color: PlayerColor, stepPosition: Int, tokenId: Int): BoardCell {
        return when {
            stepPosition < 0 -> getYardSlotCell(color, tokenId)
            stepPosition in 0..50 -> {
                val trackIndex = (getStartIndex(color) + stepPosition) % 52
                COMMON_TRACK[trackIndex]
            }
            stepPosition in 51..56 -> getHomeColumnCell(color, stepPosition)
            else -> getHomeColumnCell(color, 56)
        }
    }

    // Check if cell is a safe star cell
    fun isSafeCell(cell: BoardCell): Boolean {
        val safeCells = setOf(
            COMMON_TRACK[0],  // Red Start
            COMMON_TRACK[13], // Green Start
            COMMON_TRACK[26], // Yellow Start
            COMMON_TRACK[39], // Blue Start
            COMMON_TRACK[8],  // Left Star
            COMMON_TRACK[21], // Top Star
            COMMON_TRACK[34], // Right Star
            COMMON_TRACK[47]  // Bottom Star
        )
        return cell in safeCells
    }

    // Get list of legal token IDs that active player can move with current dice roll
    fun getLegalMoves(state: GameState, rolledValue: Int): List<Int> {
        val activePlayer = state.players[state.activePlayerIndex]
        if (activePlayer.isFinished) return emptyList()

        val legalTokenIds = mutableListOf<Int>()
        for (token in activePlayer.tokens) {
            if (token.location == TokenLocation.HOME) continue

            if (token.location == TokenLocation.YARD) {
                // Need a 6 to bring token out
                if (rolledValue == 6) {
                    legalTokenIds.add(token.id)
                }
            } else { // ON_PATH or HOME_COLUMN
                val newStep = token.stepPosition + rolledValue
                if (newStep <= 56) { // Exact roll needed to reach center (56)
                    legalTokenIds.add(token.id)
                }
            }
        }
        return legalTokenIds
    }

    // Execute move for token, handling step-by-step path, captures, extra turns, and win check
    fun executeMove(state: GameState, tokenId: Int, rolledValue: Int): GameState {
        val activePlayer = state.players[state.activePlayerIndex]
        val token = activePlayer.tokens.find { it.id == tokenId } ?: return state

        var extraTurn = (rolledValue == 6)
        var reachedHome = false
        var capturedOpponent = false

        val updatedTokens = activePlayer.tokens.toMutableList()
        val currentTokenIndex = updatedTokens.indexOfFirst { it.id == tokenId }

        val newStepPosition: Int
        val newLocation: TokenLocation

        if (token.location == TokenLocation.YARD) {
            newStepPosition = 0
            newLocation = TokenLocation.PATH
        } else {
            newStepPosition = token.stepPosition + rolledValue
            newLocation = if (newStepPosition == 56) TokenLocation.HOME else TokenLocation.PATH
        }

        if (newLocation == TokenLocation.HOME) {
            reachedHome = true
            extraTurn = true // Reaching home awards extra turn!
        }

        updatedTokens[currentTokenIndex] = token.copy(
            stepPosition = newStepPosition,
            location = newLocation
        )

        val updatedPlayers = state.players.toMutableList()
        updatedPlayers[state.activePlayerIndex] = activePlayer.copy(tokens = updatedTokens)

        // Capture logic if on common track
        if (newLocation == TokenLocation.PATH && newStepPosition in 0..50) {
            val targetCell = getTokenCell(activePlayer.color, newStepPosition, tokenId)
            if (!isSafeCell(targetCell)) {
                // Check each opponent
                for (i in updatedPlayers.indices) {
                    if (i == state.activePlayerIndex) continue
                    val opponent = updatedPlayers[i]
                    val opponentTokens = opponent.tokens.toMutableList()
                    var opponentModified = false

                    for (j in opponentTokens.indices) {
                        val oppToken = opponentTokens[j]
                        if (oppToken.location == TokenLocation.PATH && oppToken.stepPosition in 0..50) {
                            val oppCell = getTokenCell(opponent.color, oppToken.stepPosition, oppToken.id)
                            if (oppCell == targetCell) {
                                // Captured!
                                opponentTokens[j] = oppToken.copy(
                                    stepPosition = -1,
                                    location = TokenLocation.YARD
                                )
                                opponentModified = true
                                capturedOpponent = true
                                extraTurn = true // Capturing awards extra turn!
                            }
                        }
                    }

                    if (opponentModified) {
                        updatedPlayers[i] = opponent.copy(tokens = opponentTokens)
                    }
                }
            }
        }

        // Check active player finish
        val isPlayerFinished = updatedPlayers[state.activePlayerIndex].tokens.all { it.location == TokenLocation.HOME }
        var newRanks = state.winnerRanks
        if (isPlayerFinished && !activePlayer.isFinished) {
            val nextRank = state.winnerRanks.size + 1
            updatedPlayers[state.activePlayerIndex] = updatedPlayers[state.activePlayerIndex].copy(
                isFinished = true,
                rank = nextRank
            )
            newRanks = state.winnerRanks + activePlayer.color
        }

        // Check Game Over
        val activePlayersRemaining = updatedPlayers.count { !it.isFinished }
        val isGameOver = (updatedPlayers.size <= 2 && activePlayersRemaining <= 1) || (activePlayersRemaining <= 1)

        // Calculate next turn index
        var nextPlayerIndex = state.activePlayerIndex
        if (!extraTurn || isPlayerFinished) {
            // Move to next player that is not finished
            var attempts = 0
            do {
                nextPlayerIndex = (nextPlayerIndex + 1) % updatedPlayers.size
                attempts++
            } while (updatedPlayers[nextPlayerIndex].isFinished && attempts <= updatedPlayers.size)
        }

        return state.copy(
            players = updatedPlayers,
            activePlayerIndex = nextPlayerIndex,
            diceState = DiceRollState.CAN_ROLL,
            consecutiveSixes = if (rolledValue == 6 && extraTurn) state.consecutiveSixes else 0,
            winnerRanks = newRanks,
            isGameOver = isGameOver
        )
    }
}
