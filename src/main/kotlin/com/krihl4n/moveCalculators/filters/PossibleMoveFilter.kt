package com.krihl4n.moveCalculators.filters

import com.krihl4n.model.Color
import com.krihl4n.moveCalculators.PieceMoveCalculator
import com.krihl4n.moveCalculators.PossibleMove

interface PossibleMoveFilter {

    // todo refactor | maybe component which tracks colors instead of passing color here
    fun filterMoves(moveCalculator: PieceMoveCalculator, movingColor: Color, possibleMoves: Set<PossibleMove>): Set<PossibleMove>
}