package com.krihl4n

data class Message (val from: String?, val text: String?) {

    constructor() : this("default val", "default val") {

    }
}

