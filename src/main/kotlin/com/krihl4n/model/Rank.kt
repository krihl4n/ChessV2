package com.krihl4n.model

import kotlin.math.abs

data class Rank(val token: String) {

    private val allowedTokens = arrayOf("1", "2", "3", "4", "5", "6", "7", "8")

    init {
        validateToken(token)
    }

    constructor(token: Char) : this(token.toString())

    private fun validateToken(token: String) {
        if (!allowedTokens.contains(token)) {
            throw IllegalArgumentException("$token is not a rank token")
        }
    }

    operator fun plus(param: Int): Rank? {
        val index = allowedTokens.indexOf(token) + param
        if (index >= allowedTokens.size)
            return null
        return Rank(allowedTokens[index])
    }

    operator fun minus(param: Int): Rank? {
        val index = allowedTokens.indexOf(token) - param
        if (index < 0)
            return null
        return Rank(allowedTokens[index])
    }

    fun isLastFor(color: Color): Boolean {
        return color == Color.WHITE && token == "8" ||
                color == Color.BLACK && token == "1"
    }

    fun distanceTo(rank: Rank): Int {
        return abs(rank.token.toInt() - this.token.toInt())
    }
}
