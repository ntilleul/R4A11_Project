package com.example.shifumi

import kotlin.random.Random

enum class Difficulty {
    EASY,
    HARD,
    INVINCIBLE,
}

class Bot {

    private var difficulty: Difficulty = Difficulty.EASY
    private var firstPlay: Boolean = true
    private var lastMove: Symbol = Symbol.ROCK

    fun setDiffculty(difficulty: Difficulty) {
        this.difficulty = difficulty
    }

    fun getFirstPlay(): Boolean {
        return firstPlay
    }

    fun getLastMove(): Symbol {
        return lastMove
    }

    fun setLastMove(symbol: Symbol) {
        lastMove = symbol
    }

    fun play(playerSymbol: Symbol, lastWinState: WinState): Symbol {
        return when(difficulty) {
            Difficulty.EASY -> playEasy()
            Difficulty.HARD -> playHard(lastWinState)
            Difficulty.INVINCIBLE -> playInvincible(playerSymbol)
        }
    }

    private fun playEasy(): Symbol {
        return when (Random.nextInt(1, 4)) {
            1 -> Symbol.ROCK
            2 -> Symbol.PAPER
            else -> Symbol.SCISSORS
        }
    }

    // placeholder
    private fun playHard(lastWinState: WinState): Symbol {
        var moveToPlay: Symbol
        if (firstPlay) {
            firstPlay = false
            moveToPlay = playEasy()
        } else {
            if (lastWinState == WinState.PLAYER2 || lastWinState == WinState.TIE) {
                 when (lastMove) {
                    Symbol.ROCK -> moveToPlay = Symbol.PAPER
                    Symbol.PAPER -> moveToPlay = Symbol.SCISSORS
                    Symbol.SCISSORS -> moveToPlay = Symbol.ROCK
                }
            } else {
                when (lastMove) {
                    Symbol.ROCK -> moveToPlay = Symbol.SCISSORS
                    Symbol.PAPER -> moveToPlay = Symbol.ROCK
                    Symbol.SCISSORS -> moveToPlay = Symbol.PAPER
                }
            }
        }
        return moveToPlay
    }

    private fun playInvincible(playerSymbol: Symbol): Symbol {
        return when (playerSymbol) {
            Symbol.ROCK -> Symbol.PAPER
            Symbol.PAPER -> Symbol.SCISSORS
            Symbol.SCISSORS -> Symbol.ROCK
        }
    }

}