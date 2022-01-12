package com.krihl4n

import com.krihl4n.api.FieldOccupationDto
import com.krihl4n.api.GameOfChess
import org.springframework.stereotype.Service

@Service
class GameHandler : ConnectionListener{

    private val games: MutableMap<String, GameOfChess> = mutableMapOf()

    fun handleGameControlRequest(userId: String, request: String) {

    }

    override fun connectionEstablished(sessionId: String) {
        println("Register a new game for $sessionId")
        games[sessionId] = GameOfChess()
        games[sessionId]?.setupChessboard()
    }

    override fun connectionClosed(sessionId: String) {
        println("Remove game for $sessionId")
        games.remove(sessionId)
    }

    fun move(sessionId: String, from: String, to: String) {
        games[sessionId]?.move(from, to)
    }

    fun startGame(sessionId: String) {
        games[sessionId]?.start()
    }

    fun getPositions(sessionId: String): List<FieldOccupationDto>?  {
        return games[sessionId]?.getFieldOccupationInfo()
    }
}