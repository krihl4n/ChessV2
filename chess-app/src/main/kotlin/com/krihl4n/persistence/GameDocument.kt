package com.krihl4n.persistence

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "games")
data class GameDocument(@Id val id: String, val gameMode: String, val commands: MutableList<Command> = mutableListOf())

data class Command(val type: GamesRepository.CommandType, val timestamp: Instant, val data: String? = null)

data class PlayerReadyData(
    @JsonProperty("playerId") val playerId: String,
    @JsonProperty("colorPreference") val colorPreference: String?
)

data class MoveData(
    @JsonProperty("playerId") val playerId: String,
    @JsonProperty("from") val from: String,
    @JsonProperty("to") val to: String,
    @JsonProperty("pawnPromotion") val pawnPromotion: String?
)

data class ResignData(
    @JsonProperty("playerId") val playerId: String,
)
