package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.model.Field

interface MoveCalculator {

    fun calculateMoves(from: Field, positionTracker: PositionTracker): Set<PossibleMove>
}
