package com.krihl4n.turns

import com.krihl4n.model.Color

interface MovePolicy {

    fun moveAllowedBy(color: Color): Boolean
}