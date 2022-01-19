package com.krihl4n.command

import com.krihl4n.api.PiecePositionUpdateDto
import com.krihl4n.model.Move

internal interface MoveCommand {

    fun execute()

    fun undo()

    fun getMove(): Move

    fun getPiecePositionUpdate(): PiecePositionUpdateDto?
}
