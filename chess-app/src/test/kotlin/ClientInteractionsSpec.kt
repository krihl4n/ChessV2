import com.krihl4n.api.dto.GameResultDto
import com.krihl4n.api.dto.MoveDto
import com.krihl4n.api.dto.PerformedMoveDto
import com.krihl4n.api.dto.PiecePositionUpdateDto
import com.krihl4n.messages.*
import io.kotest.core.spec.style.FunSpec
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals

private const val BLACK_PLAYER_WON = "black_player_won"
private const val PLAYER_RESIGNED = "player_resigned"

class ClientInteractionsSpec : FunSpec({

    beforeTest(beforeApiTest)
    afterTest(afterApiTest)

    fun startGame(sessionId: String = SESSION_ID_1): String {
        return gameCommandHandler.requestNewGame(sessionId, StartGameRequest(VS_COMPUTER))
    }

    test("should notify player 1 that game has started when playing vs computer") {
        val gameInfoCaptor = gameStartedCaptor()

        val gameId = gameCommandHandler.requestNewGame(SESSION_ID_1, StartGameRequest(VS_COMPUTER))
        gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(gameId, WHITE))

        verify { msgSender.sendGameStartedMsg(SESSION_ID_1, any()) }
        val gameInfo = gameInfoCaptor.captured
        assertNotEquals(SESSION_ID_1, gameInfo.gameId)
        assertEquals(gameId, gameInfo.gameId)
        assertEquals(VS_COMPUTER, gameInfo.mode)
        assertEquals(WHITE, gameInfo.player.color)
        assertEquals(WHITE, gameInfo.turn)
    }

    test("should game ids be unique") {
        val gameId1 = startGame(SESSION_ID_1)
        val gameId2 = startGame(SESSION_ID_2)

        assertNotEquals(gameId1, gameId2)
    }

    test("should return correct game id") {
        val gameIdCaptor = slot<String>()
        every { msgSender.sendWaitingForOtherPlayerMsg(SESSION_ID_1, capture(gameIdCaptor)) } returns Unit

        val gameId = startGame(SESSION_ID_1)
        gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(gameId, WHITE))

        assertNotEquals("", gameId)
        assertEquals(gameId, gameIdCaptor.captured)
    }

    test("should discard session id if session closed") {
        val gameId = startGame(SESSION_ID_1)
        gameCommandHandler.connectionClosed(SESSION_ID_1)

        eventhandler.gameFinished(gameId, GameResultDto("", ""))

        verify(exactly = 0) { msgSender.sendGameFinishedMsg(SESSION_ID_1, any()) }
    }

    test("should send waiting for player event with game id") {
        val gameId = gameCommandHandler.requestNewGame(SESSION_ID_1, StartGameRequest(VS_FRIEND))

        verify { msgSender.sendWaitingForOtherPlayerMsg(SESSION_ID_1, gameId) }
    }

    test("should send game started event when second player joins") {
        val p1Captor = gameStartedCaptor(SESSION_ID_1)
        val p2Captor = gameStartedCaptor(SESSION_ID_2)
        val gameId = gameCommandHandler.requestNewGame(SESSION_ID_1, StartGameRequest(VS_FRIEND))

        gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(gameId, WHITE))
        gameCommandHandler.joinGame(SESSION_ID_2, JoinGameRequest(gameId, null))

        assertNotEquals(p1Captor.captured.player.id, p2Captor.captured.player.id)
        assertEquals(WHITE, p1Captor.captured.player.color)
        assertEquals(BLACK, p2Captor.captured.player.color)
        assertEquals(WHITE, p1Captor.captured.turn)
        assertEquals(WHITE, p2Captor.captured.turn)
    }

    test("should be able to join game with second session") {
        val gameId = gameCommandHandler.requestNewGame(SESSION_ID_1, StartGameRequest(VS_COMPUTER))
        val playerId = gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(gameId, WHITE))
        gameCommandHandler.rejoinGame(SESSION_ID_2, RejoinGameRequest(gameId, playerId))

        gameCommandHandler.move(gameId, MoveDto(playerId, "a2", "a3", null))

        verify { msgSender.sendPiecePositionUpdateMsg(SESSION_ID_1, any()) }
        verify { msgSender.sendPiecePositionUpdateMsg(SESSION_ID_2, any()) }
    }

    test("should be able to deregister session and then rejoin game") {
        val gameId = gameCommandHandler.requestNewGame(SESSION_ID_1, StartGameRequest(VS_COMPUTER))
        val playerId = gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(gameId, WHITE))
        gameCommandHandler.connectionClosed(SESSION_ID_1)
        gameCommandHandler.rejoinGame(SESSION_ID_2, RejoinGameRequest(gameId, playerId))

        gameCommandHandler.move(gameId, MoveDto(playerId, "a2", "a3", null))

        verify(exactly = 1) { msgSender.sendPiecePositionUpdateMsg(SESSION_ID_2, any()) }
        verify(exactly = 0) { msgSender.sendPiecePositionUpdateMsg(SESSION_ID_1, any()) }
    }

    test("should notify player that he has joined to existing game") {
        val gameInfoCaptor = slot<GameInfoEvent>()
        every { msgSender.sendJoinedExistingGameMsg(SESSION_ID_2, capture(gameInfoCaptor)) } returns Unit
        val gameId = gameCommandHandler.requestNewGame(SESSION_ID_1, StartGameRequest(VS_COMPUTER))
        val playerId = gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(gameId, WHITE))

        gameCommandHandler.rejoinGame(SESSION_ID_2, RejoinGameRequest(gameId, playerId))

        verify(exactly = 1) { msgSender.sendJoinedExistingGameMsg(SESSION_ID_2, any()) }
        verify(exactly = 0) { msgSender.sendJoinedExistingGameMsg(SESSION_ID_1, any()) }
        assertEquals(gameId, gameInfoCaptor.captured.gameId)
        assertEquals(playerId, gameInfoCaptor.captured.player.id)
        assertEquals(WHITE, gameInfoCaptor.captured.player.color)
        assertEquals(WHITE, gameInfoCaptor.captured.turn)
    }

    test("should notify player moved including info about current turn") {
        val gameId = gameCommandHandler.requestNewGame(SESSION_ID_1, StartGameRequest(VS_FRIEND))
        val playerId = gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(gameId, WHITE))
        gameCommandHandler.joinGame(SESSION_ID_2, JoinGameRequest(gameId, null))

        gameCommandHandler.move(gameId, MoveDto(playerId, "a2", "a4", null))

        val expectedUpdate = PiecePositionUpdateDto(
            primaryMove = PerformedMoveDto("a2", "a4"),
            secondaryMove = null,
            pieceCapture = null,
            pawnPromotion = null,
            reverted = false,
            turn = BLACK
        )
        verify(exactly = 1) { msgSender.sendPiecePositionUpdateMsg(SESSION_ID_1, expectedUpdate) }
        verify(exactly = 1) { msgSender.sendPiecePositionUpdateMsg(SESSION_ID_2, expectedUpdate) }
    }

    test("should be able to resign from the game and loose") {
        val gameId = gameCommandHandler.requestNewGame(SESSION_ID_1, StartGameRequest(VS_FRIEND))
        val playerId = gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(gameId, WHITE))
        gameCommandHandler.joinGame(SESSION_ID_2, JoinGameRequest(gameId, null))

        gameCommandHandler.resign(gameId, playerId)

        verify { msgSender.sendGameFinishedMsg(SESSION_ID_1, GameResultDto(BLACK_PLAYER_WON, PLAYER_RESIGNED)) }
        verify { msgSender.sendGameFinishedMsg(SESSION_ID_2, GameResultDto(BLACK_PLAYER_WON, PLAYER_RESIGNED)) }
    }
})
