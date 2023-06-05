package com.krihl4n.players

import com.krihl4n.model.Color

internal class FreeMovePlayersManager : PlayersManager {

    private var playerId: String? = null
    private var player: Player? = null

    override fun registerPlayer(playerId: String, colorPreference: Color?): Boolean {
        this.playerId = playerId
        player = Player(playerId, colorPreference ?: Color.WHITE)
        return true
    }

    override fun getPlayer(id: String) = player

    override fun getPlayerOne() = player ?: throw NoPlayerException()

    override fun getPlayerTwo() = player ?: throw NoPlayerException()
}