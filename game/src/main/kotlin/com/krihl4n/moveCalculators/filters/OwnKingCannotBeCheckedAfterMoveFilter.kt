package com.krihl4n.moveCalculators.filters

import com.krihl4n.Dependencies.Companion.checkGuard
import com.krihl4n.model.Color
import com.krihl4n.moveCalculators.PieceMoveCalculator
import com.krihl4n.moveCalculators.PossibleMove

internal class OwnKingCannotBeCheckedAfterMoveFilter : PossibleMoveFilter {

    override fun filterMoves(
        moveCalculator: PieceMoveCalculator,
        movingColor: Color,
        possibleMoves: Set<PossibleMove>,
    ): Set<PossibleMove> {
        return possibleMoves.filter { !checkGuard.willKingBeCheckedAfterMove(moveCalculator, movingColor, it) }.toSet()
    }
}