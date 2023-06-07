package com.krihl4n.game

internal enum class GameMode {

    TEST_MODE, VS_COMPUTER, VS_FRIEND;

    override fun toString() = super.toString().lowercase()

    companion object {
        fun fromCommand(mode: String): GameMode {
            return when (mode.lowercase()) {
                "vs_computer" -> VS_COMPUTER
                "vs_friend" -> VS_FRIEND
                else -> TEST_MODE
            }
        }
    }
}