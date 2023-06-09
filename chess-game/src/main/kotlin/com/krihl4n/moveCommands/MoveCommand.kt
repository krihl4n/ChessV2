package com.krihl4n.moveCommands

import com.krihl4n.model.Move
import com.krihl4n.model.PiecePositionUpdate

internal interface MoveCommand {

    fun execute()

    fun undo()

    fun getMove(): Move

    fun getPiecePositionUpdate(): PiecePositionUpdate?
}
