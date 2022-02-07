package com.krihl4n.players

import com.krihl4n.model.Color

class FreeMovePlayersManager : PlayersManager {

    private var playerId: String? = null
    private var player: Player? = null

    override fun registerPlayer(id: String, colorPreference: Color?): Boolean {
        playerId = id
        player = Player(id, colorPreference ?: Color.WHITE)
        return true
    }

    override fun getPlayer(id: String) = player
}