package com.krihl4n;

import java.security.InvalidParameterException

class Rank(val token: String) {

    private val allowedTokens = arrayOf("1", "2", "3", "4", "5", "6", "7", "8")

    init {
        validateToken(token)
    }

    private fun validateToken(token: String) {
        if (!allowedTokens.contains(token)) {
            throw InvalidParameterException("$token is not a rank token")
        }
    }
}
