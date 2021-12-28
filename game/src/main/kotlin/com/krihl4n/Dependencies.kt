package com.krihl4n

import com.krihl4n.castling.CastlingGuard
import com.krihl4n.guards.CheckGuard
import com.krihl4n.command.CommandCoordinator
import com.krihl4n.command.CommandFactory
import com.krihl4n.guards.EnPassantGuard

internal class Dependencies {

    companion object  {
        lateinit var positionTracker: PositionTracker
        lateinit var commandCoordinator: CommandCoordinator
        lateinit var castlingGuard: CastlingGuard
        lateinit var checkGuard: CheckGuard
        lateinit var captureTracker: CaptureTracker
        lateinit var commandFactory: CommandFactory
        lateinit var enPassantGuard: EnPassantGuard
    }

    init {
        positionTracker = PositionTracker()
        commandCoordinator = CommandCoordinator()
        castlingGuard = CastlingGuard()
        captureTracker = CaptureTracker()
        checkGuard = CheckGuard()
        commandFactory = CommandFactory()
        enPassantGuard = EnPassantGuard()
    }
}