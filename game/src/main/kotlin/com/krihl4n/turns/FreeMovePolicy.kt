package com.krihl4n.turns

import com.krihl4n.model.Color

class FreeMovePolicy: MovePolicy {

    override fun moveAllowedBy(color: Color): Boolean {
        return true
    }
}