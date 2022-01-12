package com.krihl4n

import org.springframework.stereotype.Service

@Service
interface ConnectionListener {

    fun connectionEstablished(sessionId: String)

    fun connectionClosed(sessionId: String)
}