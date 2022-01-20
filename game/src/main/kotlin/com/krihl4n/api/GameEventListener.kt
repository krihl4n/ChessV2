package com.krihl4n.api

import com.krihl4n.api.dto.PiecePositionUpdateDto

interface GameEventListener {

    fun piecePositionUpdate(sessionId: String, update: PiecePositionUpdateDto)
    // piece captured
    // pawn replaced by queen
    // undo actions
}