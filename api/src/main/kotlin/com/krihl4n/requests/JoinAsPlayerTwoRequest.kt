package com.krihl4n.requests

data class JoinAsPlayerTwoRequest(val playerId: String = "", val joinCode: String = "")


// join without code -> try to join as player two
// join with token -> verify
