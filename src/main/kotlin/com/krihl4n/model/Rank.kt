package com.krihl4n.model

class Rank {

    private val allowedTokens = arrayOf("1", "2", "3", "4", "5", "6", "7", "8")
    private val token: String

    constructor(token: String) {
        validateToken(token)
        this.token = token
    }

    constructor(token: Char) : this(token.toString())

    private fun validateToken(token: String) {
        if (!allowedTokens.contains(token)) {
            throw IllegalArgumentException("$token is not a rank token")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rank

        if (token != other.token) return false

        return true
    }

    override fun hashCode(): Int {
        return token.hashCode()
    }
}
