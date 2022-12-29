package com.krihl4n.model

import kotlin.math.abs

internal data class File(private val fileToken: String) {

    private val allowedTokens = arrayOf("a", "b", "c", "d", "e", "f", "g", "h")
    val token = fileToken.lowercase()

    init {
        validateToken(token)
    }

    constructor(token: Char) : this(token.toString())

    private fun validateToken(token: String) {
        if (!allowedTokens.contains(token))
            throw IllegalArgumentException("$token is not allowed")
    }

    operator fun plus(param: Int): File? {
        val index = allowedTokens.indexOf(token) + param
        if (index >= allowedTokens.size)
            return null
        return File(allowedTokens[index])
    }

    operator fun minus(param: Int): File? {
        val index = allowedTokens.indexOf(token) - param
        if (index < 0)
            return null
        return File(allowedTokens[index])
    }

    fun distanceTo(file: File): Int {
        return abs(allowedTokens.indexOf(this.token) - allowedTokens.indexOf(file.token))
    }
}
