package com.krihl4n.app

interface ConnectionListener {

    fun connectionEstablished(sessionId: String)

    fun connectionClosed(sessionId: String)
}