package com.krihl4n.model

internal data class Field(val file: File, val rank: Rank) {

    constructor(token: String): this(File(token[0]), Rank(token[1])) {
        if(token.length != 2)
            throw IllegalArgumentException("$token is not a valid token")
    }

    fun token() = file.token + rank.token

    override fun toString(): String {
        return "Field(${file.token}${rank.token})"
    }
}
