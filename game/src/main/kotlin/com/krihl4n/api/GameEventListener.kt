package com.krihl4n.api

import com.krihl4n.api.dto.GameInfoDto
import com.krihl4n.api.dto.GameResultDto
import com.krihl4n.api.dto.GameStateUpdateDto
import com.krihl4n.api.dto.PiecePositionUpdateDto

interface GameEventListener {

    fun piecePositionUpdate(sessionId: String, update: PiecePositionUpdateDto)

    fun gameStateUpdate(sessionId: String, update: GameStateUpdateDto)

    fun gameStarted(sessionId: String, gameInfo: GameInfoDto)

    fun gameFinished(sessionId: String, result: GameResultDto)
}