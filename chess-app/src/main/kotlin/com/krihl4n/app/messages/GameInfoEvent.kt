package com.krihl4n.app.messages

import com.krihl4n.api.dto.*

data class GameInfoEvent(
    val gameId: String,
    val mode: String,
    val player: PlayerDto,
    val piecePositions: List<FieldOccupationDto>,
    val turn: String,
    val recordedMoves: List<String>,
    val captures: CapturesDto,
    val score: ScoreDto,
    val result: GameResultDto?,
    val gameInProgress: Boolean
) {

    override fun toString(): String {
        return "GameInfoEvent(gameId='$gameId', mode='$mode', player=$player, piecePositions=$piecePositions, turn='$turn', recordedMoves=$recordedMoves, captures=$captures, score=$score, result=$result)"
    }
}
