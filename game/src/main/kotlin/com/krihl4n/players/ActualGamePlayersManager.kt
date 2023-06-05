package com.krihl4n.players

import com.krihl4n.model.Color
import java.lang.UnsupportedOperationException
import kotlin.random.Random

internal class ActualGamePlayersManager : PlayersManager {

    private var player1Id: String? = null // todo do not hold state here. make an adapter to storage maybe?
    private var player2Id: String? = null
    private var player1: Player? = null
    private var player2: Player? = null

    override fun registerPlayer(playerId: String, colorPreference: Color?): Boolean {
        if (player1 == null) {
            registerPlayerOne(colorPreference, playerId)
            return false
        }

        if (player2 == null) {
            registerPlayerTwo(playerId)
            return true
        }

        throw UnsupportedOperationException("cannot have more than 2 players")
    }

    override fun getPlayer(id: String): Player? {
        if (id == player1Id)
            return player1

        if (id == player2Id)
            return player2

        return null
    }

    override fun getPlayerOne() = player1 ?: throw NoPlayerException()

    override fun getPlayerTwo() = player2 ?: throw NoPlayerException()

    private fun registerPlayerOne(colorPreference: Color?, id: String) {
        val color = colorPreference ?: getRandomColor()
        player1Id = id
        player1 = Player(id, color)
    }

    private fun registerPlayerTwo(id: String) {
        if (player1Id == id) {
            throw IllegalArgumentException("players cannot have same ids")
        }
        val color = player1?.color?.opposite() ?: throw IllegalStateException("cannot determine player's color")
        player2Id = id
        player2 = Player(id, color)
    }

    private fun getRandomColor(): Color {
        return Color.values()[Random.nextInt(0, 2)]
    }
}

class NoPlayerException : RuntimeException()