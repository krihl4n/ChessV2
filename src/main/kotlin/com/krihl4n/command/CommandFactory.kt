package com.krihl4n.command

import com.krihl4n.model.Move

object CommandFactory {

    fun getCommand(move: Move): MoveCommand {
        return BasicMoveCommand(move)
    }
}
