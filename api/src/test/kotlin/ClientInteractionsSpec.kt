import com.krihl4n.api.dto.GameResultDto
import com.krihl4n.api.dto.PerformedMoveDto
import com.krihl4n.api.dto.PiecePositionUpdateDto
import com.krihl4n.events.GameInfoEvent
import com.krihl4n.requests.JoinGameRequest
import com.krihl4n.requests.StartGameRequest
import io.kotest.core.spec.style.FunSpec
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals

class ClientInteractionsSpec : FunSpec({

    beforeTest(beforeApiTest)
    afterTest(afterApiTest)

    fun startGame(sessionId: String = "1111"): String {
        return gameCommandHandler.requestNewGame(sessionId, StartGameRequest("vs_computer"))
    }

    test("should notify player 1 that game has started when playing vs computer") {
        val gameInfoCaptor = gameStartedCaptor()

        val gameId = gameCommandHandler.requestNewGame("999", StartGameRequest("vs_computer"))
        gameCommandHandler.joinGame("999", JoinGameRequest(gameId, "white"))

        verify { msgSender.sendGameStartedMsg("999", any()) }
        val gameInfo = gameInfoCaptor.captured
        assertNotEquals("999", gameInfo.gameId)
        assertEquals(gameId, gameInfo.gameId)
        assertEquals("VS_COMPUTER", gameInfo.mode)
        assertEquals("WHITE", gameInfo.player.color)
        assertEquals("WHITE", gameInfo.turn)
    }

    test("should game ids be unique") {
        val gameId1 = startGame("1111")
        val gameId2 = startGame("2222")

        assertNotEquals(gameId1, gameId2)
    }

    test("should return correct game id") {
        val gameIdCaptor = slot<String>()
        every { msgSender.sendWaitingForOtherPlayerMsg("888", capture(gameIdCaptor)) } returns Unit

        val gameId = startGame("888")
        gameCommandHandler.joinGame("888", JoinGameRequest(gameId, "white"))

        assertNotEquals("", gameId)
        assertEquals(gameId, gameIdCaptor.captured)
    }

    test("should discard session id if session closed") {
        val gameId = startGame("999")
        gameCommandHandler.connectionClosed("999")

        eventSender.gameFinished(gameId, GameResultDto("", ""))

        verify(exactly = 0) { msgSender.sendGameFinishedMsg("999", any()) }
    }

    test("should send waiting for player event with game id") {
        val gameId = gameCommandHandler.requestNewGame("1111", StartGameRequest("vs_friend"))

        verify { msgSender.sendWaitingForOtherPlayerMsg("1111", gameId) }
    }

    test("should send game started event when second player joins") {
        val p1Captor = gameStartedCaptor("1111")
        val p2Captor = gameStartedCaptor("2222")
        val gameId = gameCommandHandler.requestNewGame("1111", StartGameRequest("vs_friend"))

        gameCommandHandler.joinGame("1111", JoinGameRequest(gameId, "white"))
        gameCommandHandler.joinGame("2222", JoinGameRequest(gameId, null))

        assertNotEquals(p1Captor.captured.player.id, p2Captor.captured.player.id)
        assertEquals("WHITE", p1Captor.captured.player.color)
        assertEquals("BLACK", p2Captor.captured.player.color)
        assertEquals("WHITE", p1Captor.captured.turn)
        assertEquals("WHITE", p2Captor.captured.turn)
    }

    test("should be able to join game with second session") {
        val gameId = gameCommandHandler.requestNewGame("1111", StartGameRequest("vs_computer"))
        val playerId = gameCommandHandler.joinGame("1111", JoinGameRequest(gameId, "white"))
        gameCommandHandler.joinGame("2222", JoinGameRequest(gameId, null, playerId, true))

        gameCommandHandler.move("2222", playerId, "a2", "a3", null)

        verify { msgSender.sendPiecePositionUpdateMsg("1111", any()) }
        verify { msgSender.sendPiecePositionUpdateMsg("2222", any()) }
    }

    test("should be able to deregister session and then rejoin game") {
        val gameId = gameCommandHandler.requestNewGame("1111", StartGameRequest("vs_computer"))
        val playerId = gameCommandHandler.joinGame("1111", JoinGameRequest(gameId, "white"))
        gameCommandHandler.connectionClosed("1111")
        gameCommandHandler.joinGame("2222", JoinGameRequest(gameId, null, playerId, true))

        gameCommandHandler.move("2222", playerId, "a2", "a3", null)

        verify(exactly = 1) { msgSender.sendPiecePositionUpdateMsg("2222", any()) }
        verify(exactly = 0) { msgSender.sendPiecePositionUpdateMsg("1111", any()) }
    }

    test("should notify player that he has joined to existing game") {
        val gameInfoCaptor = slot<GameInfoEvent>()
        every { msgSender.sendJoinedExistingGameMsg("2222", capture(gameInfoCaptor)) } returns Unit
        val gameId = gameCommandHandler.requestNewGame("1111", StartGameRequest("vs_computer"))
        val playerId = gameCommandHandler.joinGame("1111", JoinGameRequest(gameId, "white"))

        gameCommandHandler.joinGame("2222", JoinGameRequest(gameId, null, playerId, true))

        verify(exactly = 1) { msgSender.sendJoinedExistingGameMsg("2222", any()) }
        verify(exactly = 0) { msgSender.sendJoinedExistingGameMsg("1111", any()) }
        assertEquals(gameId, gameInfoCaptor.captured.gameId)
        assertEquals(playerId, gameInfoCaptor.captured.player.id)
        assertEquals("WHITE", gameInfoCaptor.captured.player.color)
        assertEquals("WHITE", gameInfoCaptor.captured.turn)
    }

    test("should notify player moved including info about current turn") {
        val gameId = gameCommandHandler.requestNewGame("1111", StartGameRequest("vs_friend"))
        val playerId = gameCommandHandler.joinGame("1111", JoinGameRequest(gameId, "white"))
        gameCommandHandler.joinGame("2222", JoinGameRequest(gameId, null))

        gameCommandHandler.move("1111", playerId, "a2", "a4", null)

        val expectedUpdate = PiecePositionUpdateDto(
            primaryMove = PerformedMoveDto("a2", "a4"),
            secondaryMove = null,
            pieceCapture = null,
            pawnPromotion = null,
            reverted = false,
            turn = "BLACK"
        )
        verify(exactly = 1) { msgSender.sendPiecePositionUpdateMsg("1111", expectedUpdate) }
        verify(exactly = 1) { msgSender.sendPiecePositionUpdateMsg("2222", expectedUpdate) }
    }

    test("should be able to resign from the game and loose") {
        val gameId = gameCommandHandler.requestNewGame("1111", StartGameRequest("vs_friend"))
        val playerId = gameCommandHandler.joinGame("1111", JoinGameRequest(gameId, "white"))
        gameCommandHandler.joinGame("2222", JoinGameRequest(gameId, null))

        gameCommandHandler.resign("1111", playerId)

        verify { msgSender.sendGameFinishedMsg("1111", GameResultDto("BLACK_PLAYER_WON", "PLAYER_RESIGNED")) }
        verify { msgSender.sendGameFinishedMsg("2222", GameResultDto("BLACK_PLAYER_WON", "PLAYER_RESIGNED")) }
    }
})
