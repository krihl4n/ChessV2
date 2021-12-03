package com.krihl4n.command

import com.krihl4n.model.Move

interface MoveCommand {

    fun execute()

    fun undo()

    fun getMove(): Move
}
