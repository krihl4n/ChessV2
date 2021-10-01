package com.krihl4n.model

data class File(private val fileToken: String) {

    private val allowedTokens = arrayOf("a", "b", "c", "d", "e", "f", "g", "h")
    val token = fileToken.toLowerCase()

    init {
        validateToken(token)
    }

    constructor(token: Char) : this(token.toString())

    private fun validateToken(token: String) {
        if (!allowedTokens.contains(token))
            throw IllegalArgumentException("$token is not allowed")
    }
}
