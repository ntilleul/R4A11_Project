package com.example.shifumi

import kotlin.random.Random

enum class Difficulty {
    EASY,
    HARD,
    INVINCIBLE,
}

class Bot {

    private var difficulty: Difficulty = Difficulty.EASY
    private var lastMove: Symbol = Symbol.ROCK
    private var lastWinState: WinState = WinState.TIE

    fun setDiffculty(difficulty: Difficulty) {
        this.difficulty = difficulty
    }

    fun getDiffculty(): Difficulty {
        return this.difficulty
    }

    fun play(playerSymbol: Symbol): Symbol {
        return when(difficulty) {
            Difficulty.EASY -> playEasy()
            Difficulty.HARD -> playHard()
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
    private fun playHard(): Symbol {
        return Symbol.ROCK
    }

    private fun playInvincible(playerSymbol: Symbol): Symbol {
        return when (playerSymbol) {
            Symbol.ROCK -> Symbol.PAPER
            Symbol.PAPER -> Symbol.SCISSORS
            Symbol.SCISSORS -> Symbol.ROCK
        }
    }

}