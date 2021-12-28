package com.krihl4n

fun main() {

    val game = GameCoordinator()
    val playerOne = InMemoryPlayer()
    val playerTwo = InMemoryPlayer()

    game.registerPlayer(playerOne)
    game.registerPlayer(playerTwo)

    game.startGame()

    game.move(playerOne.id!!, "a2","a3")

}

class InMemoryPlayer: Player {

    var id: String? = null

    override fun idAssigned(id: String) {
        this.id = id
    }

    override fun notifyPlayersTurn() {
        TODO("Not yet implemented")
    }
}
