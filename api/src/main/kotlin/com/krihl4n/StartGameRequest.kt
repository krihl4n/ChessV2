package com.krihl4n

data class StartGameRequest(val playerId: String = "", val mode: String = "", val colorPreference: String? = null)
