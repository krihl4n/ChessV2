package com.krihl4n.requests

data class StartGameRequest(val playerId: String = "", val mode: String = "", val colorPreference: String? = null)
