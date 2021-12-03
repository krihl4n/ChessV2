package com.krihl4n.command

import com.krihl4n.model.Move

interface MoveObserver {

    fun movePerformed(move: Move)

    fun moveUndid(move: Move)
}