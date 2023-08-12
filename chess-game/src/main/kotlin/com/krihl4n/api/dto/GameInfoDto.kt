package com.krihl4n.api.dto

data class GameInfoDto(
    val gameId: String,
    val mode: String,
    val player1: PlayerDto,
    val player2: PlayerDto,
    val piecePositions: List<FieldOccupationDto>,
    val turn: String,
    val recordedMoves: List<String>
)
