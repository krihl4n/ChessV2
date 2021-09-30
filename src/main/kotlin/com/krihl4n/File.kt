package com.krihl4n

class File {

    val token: String

    constructor(token: String) {
        this.token = token.toLowerCase()
        validateToken(this.token)
    }

    constructor(token: Char) : this(token.toString())

    private val allowedTokens = arrayOf("a", "b", "c", "d", "e", "f", "g", "h")

    private fun validateToken(token: String) {
        if (!allowedTokens.contains(token))
            throw IllegalArgumentException("$token is not allowed")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as File

        if (token != other.token) return false

        return true
    }

    override fun hashCode(): Int {
        return token.hashCode()
    }
}
