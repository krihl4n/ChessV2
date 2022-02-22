package com.krihl4n.api

import com.krihl4n.api.dto.GameStateUpdateDto
import com.krihl4n.api.dto.PiecePositionUpdateDto

interface GameEventListener {

    fun piecePositionUpdate(sessionId: String, update: PiecePositionUpdateDto)

    fun gameStateUpdate(sessionId: String, update: GameStateUpdateDto)
}