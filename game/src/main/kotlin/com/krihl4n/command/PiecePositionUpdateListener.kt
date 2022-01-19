package com.krihl4n.command;

import com.krihl4n.model.PiecePositionUpdate

internal interface PiecePositionUpdateListener {

    fun positionsUpdated(update: PiecePositionUpdate)
}
