package com.example.mludo.logic

import android.content.Context
import com.example.mludo.model.DiceRollState
import com.example.mludo.model.GameMode
import com.example.mludo.model.GameState
import com.example.mludo.model.PairOption
import com.example.mludo.model.PlayerColor
import com.example.mludo.model.PlayerCountOption
import com.example.mludo.model.PlayerState
import com.example.mludo.model.TokenLocation
import com.example.mludo.model.TokenState
import org.json.JSONArray
import org.json.JSONObject

object LudoStorage {
    private const val PREF_NAME = "mludo_game_prefs"
    private const val KEY_SAVED_STATE = "saved_game_json"

    fun saveGame(context: Context, state: GameState) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = JSONObject().apply {
            put("gameId", state.gameId)
            put("mode", state.mode.name)
            put("playerCount", state.playerCount.name)
            put("pairOption", state.pairOption.name)
            put("oneTokenOut", state.oneTokenOut)
            put("activePlayerIndex", state.activePlayerIndex)
            put("diceValue", state.diceValue)
            put("diceState", state.diceState.name)
            put("consecutiveSixes", state.consecutiveSixes)
            put("isGameOver", state.isGameOver)
            put("soundEnabled", state.soundEnabled)

            val playersArr = JSONArray()
            for (p in state.players) {
                val pObj = JSONObject().apply {
                    put("color", p.color.name)
                    put("name", p.name)
                    put("rank", p.rank)
                    put("isFinished", p.isFinished)

                    val tokensArr = JSONArray()
                    for (t in p.tokens) {
                        val tObj = JSONObject().apply {
                            put("id", t.id)
                            put("color", t.color.name)
                            put("stepPosition", t.stepPosition)
                            put("location", t.location.name)
                        }
                        tokensArr.put(tObj)
                    }
                    put("tokens", tokensArr)
                }
                playersArr.put(pObj)
            }
            put("players", playersArr)
        }

        prefs.edit().putString(KEY_SAVED_STATE, json.toString()).apply()
    }

    fun loadGame(context: Context): GameState? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_SAVED_STATE, null) ?: return null
        return try {
            val json = JSONObject(jsonStr)
            val gameId = json.optString("gameId", "")
            val mode = GameMode.valueOf(json.optString("mode", GameMode.CLASSIC.name))
            val playerCount = PlayerCountOption.valueOf(json.optString("playerCount", PlayerCountOption.P2.name))
            val pairOption = PairOption.valueOf(json.optString("pairOption", PairOption.RED_YELLOW.name))
            val oneTokenOut = json.optBoolean("oneTokenOut", false)
            val activePlayerIndex = json.optInt("activePlayerIndex", 0)
            val diceValue = json.optInt("diceValue", 1)
            val diceState = DiceRollState.valueOf(json.optString("diceState", DiceRollState.CAN_ROLL.name))
            val consecutiveSixes = json.optInt("consecutiveSixes", 0)
            val isGameOver = json.optBoolean("isGameOver", false)
            val soundEnabled = json.optBoolean("soundEnabled", true)

            val playersArr = json.getJSONArray("players")
            val players = mutableListOf<PlayerState>()
            for (i in 0 until playersArr.length()) {
                val pObj = playersArr.getJSONObject(i)
                val color = PlayerColor.valueOf(pObj.getString("color"))
                val name = pObj.getString("name")
                val rank = pObj.optInt("rank", 0)
                val isFinished = pObj.optBoolean("isFinished", false)

                val tokensArr = pObj.getJSONArray("tokens")
                val tokens = mutableListOf<TokenState>()
                for (j in 0 until tokensArr.length()) {
                    val tObj = tokensArr.getJSONObject(j)
                    val id = tObj.getInt("id")
                    val tColor = PlayerColor.valueOf(tObj.getString("color"))
                    val stepPosition = tObj.getInt("stepPosition")
                    val location = TokenLocation.valueOf(tObj.getString("location"))
                    tokens.add(TokenState(id, tColor, stepPosition, location))
                }
                players.add(PlayerState(color, name, tokens, rank, isFinished))
            }

            GameState(
                gameId = gameId,
                mode = mode,
                playerCount = playerCount,
                pairOption = pairOption,
                oneTokenOut = oneTokenOut,
                players = players,
                activePlayerIndex = activePlayerIndex,
                diceValue = diceValue,
                diceState = diceState,
                consecutiveSixes = consecutiveSixes,
                isGameOver = isGameOver,
                soundEnabled = soundEnabled
            )
        } catch (e: Exception) {
            null
        }
    }

    fun hasSavedGame(context: Context): Boolean {
        return loadGame(context) != null
    }

    fun clearSavedGame(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().remove(KEY_SAVED_STATE).apply()
    }
}
