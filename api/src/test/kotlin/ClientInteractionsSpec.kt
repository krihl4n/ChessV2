import com.krihl4n.*
import com.krihl4n.api.dto.GameResultDto
import com.krihl4n.api.dto.MoveDto
import com.krihl4n.api.dto.PiecePositionUpdateDto
import com.krihl4n.api.dto.PlayerDto
import com.krihl4n.app.MessageSender
import com.krihl4n.events.GameInfoEvent
import com.krihl4n.requests.JoinGameRequest
import com.krihl4n.requests.StartGameRequest
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals

class ClientInteractionsSpec : ShouldSpec({
    val msgSender = mockk<MessageSender>(relaxed = true)
    var gamesRegister = GamesRegister()
    var eventSender = GameEventHandler(msgSender, gamesRegister)
    var gameCommandHandler = GameCommandHandler(eventSender, gamesRegister)

    beforeTest {
        gamesRegister = GamesRegister()
        eventSender = GameEventHandler(msgSender, gamesRegister)
        gameCommandHandler = GameCommandHandler(eventSender, gamesRegister)
    }

    afterTest { clearAllMocks() }

    fun startGame(sessionId: String = "1111"): String {
        return gameCommandHandler.requestNewGame(sessionId, StartGameRequest("vs_computer"))
    }

    should("notify player 1 that game has started when playing vs computer") {
        val gameInfoCaptor = slot<GameInfoEvent>()
        every { msgSender.sendGameStartedMsg(any(), capture(gameInfoCaptor)) } returns Unit

        val gameId = gameCommandHandler.requestNewGame("999", StartGameRequest("vs_computer"))
        gameCommandHandler.joinGame("999", JoinGameRequest(gameId, "white"))

        verify { msgSender.sendGameStartedMsg("999", any()) }
        assertNotEquals("999", gameInfoCaptor.captured.gameId)
        assertEquals(gameId, gameInfoCaptor.captured.gameId)
        assertEquals("VS_COMPUTER", gameInfoCaptor.captured.mode)
        assertEquals("WHITE", gameInfoCaptor.captured.player.color)
        assertEquals("WHITE", gameInfoCaptor.captured.turn)
    }

    should("game ids be unique") {
        val gameId1 = startGame("1111")
        val gameId2 = startGame("2222")

        assertNotEquals(gameId1, gameId2)
    }

    should("return correct game id") {
        val gameInfoCaptor = slot<String>()
        every { msgSender.sendWaitingForOtherPlayerMsg("888", capture(gameInfoCaptor)) } returns Unit

        val gameId = startGame("888")
        gameCommandHandler.joinGame("888", JoinGameRequest(gameId, "white"))

        assertNotEquals("", gameId)
        assertEquals(gameId, gameInfoCaptor.captured)
    }

    should("discard session id if session closed") {
        val gameId = startGame("999")
        gameCommandHandler.connectionClosed("999")

        eventSender.gameFinished(gameId, GameResultDto("", ""))

        verify(exactly = 0) { msgSender.sendGameFinishedMsg("999", any()) }
    }

    should("send waiting for player event with game id") {
        val gameId = gameCommandHandler.requestNewGame("1111", StartGameRequest("vs_friend"))

        verify { msgSender.sendWaitingForOtherPlayerMsg("1111", gameId) }
    }

    should("send game started event when second player joins") {
        val gameInfoPlayer1Captor = slot<GameInfoEvent>()
        every { msgSender.sendGameStartedMsg("1111", capture(gameInfoPlayer1Captor)) } returns Unit
        val gameInfoPlayer2Captor = slot<GameInfoEvent>()
        every { msgSender.sendGameStartedMsg("2222", capture(gameInfoPlayer2Captor)) } returns Unit
        val gameId = gameCommandHandler.requestNewGame("1111", StartGameRequest("vs_friend"))

        gameCommandHandler.joinGame("1111", JoinGameRequest(gameId, "white"))
        gameCommandHandler.joinGame("2222", JoinGameRequest(gameId, null))

        assertNotEquals(gameInfoPlayer1Captor.captured.player.id, gameInfoPlayer2Captor.captured.player.id)
        assertEquals("WHITE", gameInfoPlayer1Captor.captured.player.color)
        assertEquals("BLACK", gameInfoPlayer2Captor.captured.player.color)
        assertEquals("WHITE", gameInfoPlayer1Captor.captured.turn)
        assertEquals("WHITE", gameInfoPlayer2Captor.captured.turn)
    }

    should("be able to join game with second session") {
        val gameId = gameCommandHandler.requestNewGame("1111", StartGameRequest("vs_computer"))
        val playerId = gameCommandHandler.joinGame("1111", JoinGameRequest(gameId, "white"))
        gameCommandHandler.joinGame("2222", JoinGameRequest(gameId, null, playerId))

        gameCommandHandler.move("2222", playerId, "a2", "a3")

        verify { msgSender.sendPiecePositionUpdateMsg("1111", any()) }
        verify { msgSender.sendPiecePositionUpdateMsg("2222", any()) }
    }

    should("be able to deregister session and then rejoin game") {
        val gameId = gameCommandHandler.requestNewGame("1111", StartGameRequest("vs_computer"))
        val playerId = gameCommandHandler.joinGame("1111", JoinGameRequest(gameId, "white"))
        gameCommandHandler.connectionClosed("1111")
        gameCommandHandler.joinGame("2222", JoinGameRequest(gameId, null, playerId))

        gameCommandHandler.move("2222", playerId, "a2", "a3")

        verify(exactly = 1) { msgSender.sendPiecePositionUpdateMsg("2222", any()) }
        verify(exactly = 0) { msgSender.sendPiecePositionUpdateMsg("1111", any()) }
    }

    should("notify player that he has joined to existing game") {
        val gameInfoCaptor = slot<GameInfoEvent>()
        every { msgSender.sendJoinedExistingGameMsg("2222", capture(gameInfoCaptor)) } returns Unit
        val gameId = gameCommandHandler.requestNewGame("1111", StartGameRequest("vs_computer"))
        val playerId = gameCommandHandler.joinGame("1111", JoinGameRequest(gameId, "white"))

        gameCommandHandler.joinGame("2222", JoinGameRequest(gameId, null, playerId))

        verify(exactly = 1) { msgSender.sendJoinedExistingGameMsg("2222", any()) }
        verify(exactly = 0) { msgSender.sendJoinedExistingGameMsg("1111", any()) }
        assertEquals(gameId, gameInfoCaptor.captured.gameId)
        assertEquals(playerId, gameInfoCaptor.captured.player.id)
        assertEquals("WHITE", gameInfoCaptor.captured.player.color)
        assertEquals("WHITE", gameInfoCaptor.captured.turn)
    }

    should("notify player moved including info about current turn") {
        val gameId = gameCommandHandler.requestNewGame("1111", StartGameRequest("vs_friend"))
        val playerId = gameCommandHandler.joinGame("1111", JoinGameRequest(gameId, "white"))
        gameCommandHandler.joinGame("2222", JoinGameRequest(gameId, null))

        gameCommandHandler.move("1111", playerId, "a2", "a4")

        val expectedUpdate = PiecePositionUpdateDto(
            primaryMove = MoveDto("a2", "a4"),
            secondaryMove = null,
            pieceCapture = null,
            convertToQueen = false,
            reverted = false,
            turn = "BLACK"
        )
        verify(exactly = 1) { msgSender.sendPiecePositionUpdateMsg("1111", expectedUpdate) }
        verify(exactly = 1) { msgSender.sendPiecePositionUpdateMsg("2222", expectedUpdate) }
    }
})
