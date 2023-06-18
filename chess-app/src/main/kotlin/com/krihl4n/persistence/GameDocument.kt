package com.krihl4n.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "games")
data class GameDocument(@Id val id: String, val gameMode: String, val commands: MutableList<Command> = mutableListOf())

data class Command(val type: String, val timestamp: Instant, val data: String? = null)

data class PlayerReadyData(val colorPreference: String?)

data class MoveData(val playerId: String, val from: String, val to: String, val pawnPromotion: String?)
