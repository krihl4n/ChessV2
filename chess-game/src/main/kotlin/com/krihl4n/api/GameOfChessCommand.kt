package com.krihl4n.api

import com.krihl4n.api.dto.MoveDto

interface GameOfChessCommand {

    fun initialize()
    fun playerReady(playerId: String, colorPreference: String?)
    fun resign(playerId: String)
    fun move(move: MoveDto)
    fun undoMove()
    fun redoMove()
    fun registerGameEventListener(listener: GameEventListener)}