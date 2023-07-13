package com.krihl4n.game.positionEvaluators

import com.krihl4n.model.Move
import com.krihl4n.model.Type

internal class FiftyMoveRepetitionEvaluator {

    private var moves = ArrayDeque<Move>()

    fun occurs(move: Move): Boolean {
        moves.add(move)
        var moveRepetitionCounter = 0
        moves.forEach { m ->
            if (m.piece.type == Type.PAWN || m.isAttack) {
                moveRepetitionCounter = 0
            }
            moveRepetitionCounter++
        }
        return moveRepetitionCounter == 100
    }

    fun moveUndid() {
        moves.removeLast()
    }
}