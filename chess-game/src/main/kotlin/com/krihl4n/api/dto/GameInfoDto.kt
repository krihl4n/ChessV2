package com.krihl4n.api.dto

data class GameInfoDto(
    val gameId: String,
    val mode: String,
    val player1: PlayerDto,
    val player2: PlayerDto,
    val piecePositions: List<FieldOccupationDto>,
    val turn: String,
    val recordedMoves: List<String>,
    val captures: CapturesDto,
    val score: ScoreDto,
    val gameInProgress: Boolean,
)

data class CapturesDto (
    val capturesOfWhitePlayer: List<PieceDto>,
    val capturesOfBlackPlayer: List<PieceDto>
)

data class ScoreDto (val white: Int, val black: Int)
