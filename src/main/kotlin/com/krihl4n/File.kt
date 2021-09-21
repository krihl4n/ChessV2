package com.krihl4n

class File(token: String) {

    private val allowedTokens = arrayOf("a", "b", "c", "d", "e", "f", "g", "h")
    val token: String = token.toLowerCase()

    init {
        validateToken(this.token)
    }

    private fun validateToken(token: String) {
        if (!allowedTokens.contains(token))
            throw IllegalArgumentException("$token is not allowed")
    }
}
