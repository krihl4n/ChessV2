package com.krihl4n.api.dto

data class ColorDto(val value: String) {
    fun opposite() = if(value.lowercase() == "white") ColorDto("black") else ColorDto("white")
}