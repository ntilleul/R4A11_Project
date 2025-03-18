package com.example.shifumi

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import java.text.DecimalFormat
import kotlin.random.Random

enum class Symbol {
    ROCK,
    PAPER,
    SCISSORS,
}

enum class WinState {
    PLAYER1,
    TIE,
    PLAYER2,
}

class GameViewModel : ViewModel() {
    var velocityX = 0.0F
    var velocityY = 0.0F
    var velocityZ = 0.0F
    var shakeThreshold = 50.0F
    var nShake by mutableIntStateOf(0)
    var countDownString by mutableStateOf("3")
    var bot by mutableStateOf(Bot())
    var symbolPlayer by  mutableStateOf(Symbol.ROCK)
    var symbolBot by  mutableStateOf(Symbol.ROCK)

    fun reset() {
        this.nShake = 0
        this.countDownString = "3"
    }

    fun randomSymbol(): Symbol {
        return when (Random.nextInt(1, 4)) {
            1 -> Symbol.ROCK
            2 -> Symbol.PAPER
            else -> Symbol.SCISSORS
        }
    }

    fun getWinner(playerOneSymbol: Symbol, playerTwoSymbol: Symbol): WinState {
        var res = WinState.TIE
        if (playerOneSymbol == playerTwoSymbol) {
            res = WinState.TIE
        } else {
            // symbol left looses to symbol right
            val rules = mapOf(
                Symbol.ROCK to Symbol.PAPER,
                Symbol.PAPER to Symbol.SCISSORS,
                Symbol.SCISSORS to Symbol.ROCK,
            )

            if (playerOneSymbol == rules[playerTwoSymbol]) {
                res = WinState.PLAYER1
            } else if (playerTwoSymbol == rules[playerOneSymbol]) {
                res = WinState.PLAYER2
            }
        }
        return res
    }

    fun getVelocity(): Float {
        return velocityX + velocityY + velocityZ
    }
}