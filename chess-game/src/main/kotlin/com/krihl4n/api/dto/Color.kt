package com.krihl4n.api.dto

enum class Color(private val value: String) {

    WHITE("white"), BLACK("black");

    fun getValue(): String {
        return this.value
    }
}