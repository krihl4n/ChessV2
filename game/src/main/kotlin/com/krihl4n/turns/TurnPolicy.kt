package com.krihl4n.turns

import com.krihl4n.model.Color

interface TurnPolicy {

    fun moveAllowedBy(color: Color): Boolean
}