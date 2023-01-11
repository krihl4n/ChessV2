package com.krihl4n.events

import com.krihl4n.api.dto.FieldOccupationDto
import com.krihl4n.api.dto.PlayerDto

data class GameInfoEvent(val gameId: String, val mode: String, val player: PlayerDto, val piecePositions: List<FieldOccupationDto>)
