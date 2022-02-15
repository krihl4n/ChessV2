package com.krihl4n.moveCommands

import com.krihl4n.model.Move

internal interface MoveObserver {

    fun movePerformed(move: Move)

    fun moveUndid(move: Move)
}