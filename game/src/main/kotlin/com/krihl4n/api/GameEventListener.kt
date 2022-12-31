package com.krihl4n.api

import com.krihl4n.api.dto.GameInfoDto
import com.krihl4n.api.dto.GameResultDto
import com.krihl4n.api.dto.GameStateUpdateDto
import com.krihl4n.api.dto.PiecePositionUpdateDto

interface GameEventListener {

    fun piecePositionUpdate(gameId: String, update: PiecePositionUpdateDto)

    fun gameStateUpdate(gameId: String, update: GameStateUpdateDto)

    fun gameStarted(gameId: String, gameInfo: GameInfoDto)

    fun gameFinished(gameId: String, result: GameResultDto)
}