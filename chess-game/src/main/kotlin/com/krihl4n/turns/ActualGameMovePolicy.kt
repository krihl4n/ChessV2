package com.krihl4n.turns

import com.krihl4n.moveCommands.MoveObserver
import com.krihl4n.model.Color
import com.krihl4n.model.Move
import com.krihl4n.players.PlayersManager

internal class ActualGameMovePolicy(private val playersManager: PlayersManager) : MovePolicy, MoveObserver {

    private var colorAllowedToMove = Color.WHITE

    override fun moveAllowedBy(playerId: String, move: Move): Boolean {
        val playerColor = playersManager.getPlayer(playerId)?.color
        return playerColor == colorAllowedToMove && playerColor == move.piece.color
    }

    override fun colorAllowedToMove(): Color {
        return this.colorAllowedToMove
    }

    override fun movePerformed(move: Move) {
        this.colorAllowedToMove = move.piece.color.opposite()
    }

    override fun moveUndid(move: Move) {
        this.colorAllowedToMove = move.piece.color
    }
}