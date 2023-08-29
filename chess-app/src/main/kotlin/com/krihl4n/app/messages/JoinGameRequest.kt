package com.krihl4n.app.messages

import com.krihl4n.api.dto.Color

data class JoinGameRequest(
    val gameId: String = "",
    val colorPreference: Color? = null,
    val playerId: String? = null
)
