package com.krihl4n.moveCommands;

import com.krihl4n.model.PiecePositionUpdate

internal interface PiecePositionUpdateListener {

    fun positionsUpdated(update: PiecePositionUpdate)
}
