package com.krihl4n.requests

data class Move (val playerId:String = "", val from: String = "", val to: String = "", val pawnPromotion: String? = null)
