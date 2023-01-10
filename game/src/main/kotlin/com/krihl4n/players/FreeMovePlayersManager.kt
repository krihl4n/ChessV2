package com.krihl4n.players

import com.krihl4n.model.Color
import java.util.*

class FreeMovePlayersManager : PlayersManager {

    private var playerId: String? = null
    private var player: Player? = null

    override fun registerPlayer(colorPreference: Color?): Boolean {
        playerId = UUID.randomUUID().toString()
        player = Player(playerId!!, colorPreference ?: Color.WHITE)
        return true
    }

    override fun getPlayer(id: String) = player

    override fun getPlayerOne() = player ?: throw NoPlayerException()

    override fun getPlayerTwo() = player ?: throw NoPlayerException()
}