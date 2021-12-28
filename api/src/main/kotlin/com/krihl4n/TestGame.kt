package com.krihl4n

fun main() {
    val game = GameOfChess()
    game.start()
    game.move("a2", "a3")
    game.finish()
}