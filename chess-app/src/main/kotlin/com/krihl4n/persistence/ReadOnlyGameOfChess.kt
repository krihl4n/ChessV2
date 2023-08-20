package com.krihl4n.persistence

import com.krihl4n.api.GameOfChess
import com.krihl4n.api.GameOfChessQuery

open class ReadOnlyGameOfChess(private val delegate: GameOfChess) : GameOfChessQuery {

    override fun getMode() = delegate.getMode()
    override fun getPlayer(playerId: String) = delegate.getPlayer(playerId)
    override fun getPlayers() = delegate.getPlayers()
    override fun getColorAllowedToMove() = delegate.getColorAllowedToMove()
    override fun getFieldOccupationInfo() = delegate.getFieldOccupationInfo()
    override fun getPossibleMoves(field: String) = delegate.getPossibleMoves(field)
    override fun getRecordedMoves() = delegate.getRecordedMoves()
    override fun getAllCaptures() = delegate.getAllCaptures()
    override fun getScore() = delegate.getScore()
    override fun getResult() = delegate.getResult()

    fun gameId() = delegate.gameId
    fun gameMode() = delegate.gameMode
}