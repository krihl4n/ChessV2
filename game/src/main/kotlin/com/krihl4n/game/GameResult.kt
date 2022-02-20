package com.krihl4n.game

class GameResult(val result: Result, val reason: ResultReason) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameResult

        if (result != other.result) return false
        if (reason != other.reason) return false

        return true
    }

    override fun hashCode(): Int {
        var result1 = result.hashCode()
        result1 = 31 * result1 + reason.hashCode()
        return result1
    }

    override fun toString(): String {
        return "GameResult(result=$result, reason=$reason)"
    }
}

enum class Result{
    WHITES_WON,
    BLACKS_WON,
    DRAW
}

enum class ResultReason{
    CHECK_MATE,
    FORFEIT,
    STALEMATE,
    INSUFFICIENT_MATERIAL
}