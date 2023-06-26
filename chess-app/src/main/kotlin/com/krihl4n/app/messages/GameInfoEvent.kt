package com.krihl4n.app.messages

import com.krihl4n.api.dto.FieldOccupationDto
import com.krihl4n.api.dto.PlayerDto

data class GameInfoEvent(val gameId: String, val mode: String, val player: PlayerDto, val piecePositions: List<FieldOccupationDto>, val turn: String){

    override fun toString(): String {
        return "GameInfoEvent(gameId='$gameId', mode='$mode', player=$player, turn='$turn')"
    }
}
