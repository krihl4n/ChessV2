package com.krihl4n.requests

data class JoinGameRequest(
    val gameId: String = "",
    val colorPreference: String? = null,
    val playerId: String? = null,
    val rejoin: Boolean = false
    // separate playerId from game, and make it immutable during subsequent rematches
    // should make color switching easier
)
