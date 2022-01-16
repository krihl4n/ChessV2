package com.krihl4n

interface ConnectionListener {

    fun connectionEstablished(sessionId: String)

    fun connectionClosed(sessionId: String)
}