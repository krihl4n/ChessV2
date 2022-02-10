package com.krihl4n.api.dto

enum class GameModeDto {

    FREE_MOVE, HOT_SEAT, VS_COMPUTER;

    companion object {
        fun fromCommand(cmd: String): GameModeDto {
            return if (cmd.lowercase().endsWith("hot_seat")) {
                HOT_SEAT
            } else if (cmd.lowercase().endsWith("vs_computer")) {
                VS_COMPUTER
            } else {
                FREE_MOVE
            }
        }
    }
}