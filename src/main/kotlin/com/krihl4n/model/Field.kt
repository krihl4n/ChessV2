package com.krihl4n.model

class Field {

    private val file: File
    private val rank: Rank

    constructor(file: File, rank: Rank) {
        this.file = file
        this.rank = rank
    }

    constructor(token: String): this(File(token[0]), Rank(token[1])) {
        if(token.length != 2)
            throw IllegalArgumentException("$token is not a valid token")
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Field

        if (file != other.file) return false
        if (rank != other.rank) return false

        return true
    }

    override fun hashCode(): Int {
        var result = file.hashCode()
        result = 31 * result + rank.hashCode()
        return result
    }
}

