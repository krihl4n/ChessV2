package com.krihl4n.api

import com.krihl4n.api.dto.*

interface GameOfChessQuery {

    fun getMode(): String
    fun getPlayer(playerId: String): PlayerDto?
    fun getPlayers(): List<PlayerDto>
    fun getColorAllowedToMove(): String
    fun getFieldOccupationInfo(): List<FieldOccupationDto>
    fun getPossibleMoves(field: String): PossibleMovesDto
    fun getRecordedMoves(): List<String>
    fun getAllCaptures(): CapturesDto
    fun getScore(): ScoreDto
}