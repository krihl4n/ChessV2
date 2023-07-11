package com.krihl4n.game.result

data class GameResult(val result: Result, val reason: ResultReason)

enum class Result{
    WHITE_PLAYER_WON,
    BLACK_PLAYER_WON,
    DRAW
}

enum class ResultReason{
    CHECK_MATE,
    STALEMATE,
    DEAD_POSITION,
    INSUFFICIENT_MATERIAL,
    PLAYER_RESIGNED,
    REPETITION
}
