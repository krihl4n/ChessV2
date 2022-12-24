package com.krihl4n.api.dto

enum class GameModeDto {

    TEST_MODE, VS_COMPUTER, VS_FRIEND;

    companion object {
        fun fromCommand(mode: String): GameModeDto {
            return when (mode.lowercase()) {
                "vs_computer" -> VS_COMPUTER
                "vs_friend" -> VS_FRIEND
                else -> TEST_MODE
            }
        }
    }
}