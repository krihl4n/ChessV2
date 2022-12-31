package com.krihl4n.api.dto

import com.krihl4n.players.Player

data class GameInfoDto(val gameId: String, val mode: String, val player1: PlayerDto, val player2: PlayerDto, val piecePositions: List<FieldOccupationDto>)

data class PlayerDto(val id: String, val color: String) {
    companion object{
        fun from(player: Player) : PlayerDto {
            return PlayerDto(player.id, player.color.toString())
        }
    }
}
