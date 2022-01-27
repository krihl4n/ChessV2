package com.krihl4n.turns

import com.krihl4n.model.Color

class FreeTurnPolicy: TurnPolicy {

    override fun moveAllowedBy(color: Color): Boolean {
        return true
    }
}