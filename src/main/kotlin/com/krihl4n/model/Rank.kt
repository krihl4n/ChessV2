package com.krihl4n.model

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
}
