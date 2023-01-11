import com.krihl4n.*
import com.krihl4n.api.dto.GameResultDto
import com.krihl4n.app.MessageSender
import com.krihl4n.events.GameInfoEvent
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

    fun startGame(sessionId: String = "1111"): String {
        return gameCommandHandler.requestNewGame(sessionId, StartGameRequest("vs_computer", "white"))
    }

    should("notify player 1 that game has started when playing vs computer") {
        val gameInfoCaptor = slot<GameInfoEvent>()
        every { msgSender.sendGameStartedMsg(any(), capture(gameInfoCaptor)) } returns Unit

        val gameId = gameCommandHandler.requestNewGame("999", StartGameRequest("vs_computer", "white"))

        verify { msgSender.sendGameStartedMsg("999", any()) }
        assertNotEquals("999", gameInfoCaptor.captured.gameId)
        assertEquals(gameId, gameInfoCaptor.captured.gameId)
        assertEquals("VS_COMPUTER", gameInfoCaptor.captured.mode)
        assertEquals("WHITE", gameInfoCaptor.captured.player.color)
    }

    should("game ids be unique") {
        val gameId1 = startGame("1111")
        val gameId2 = startGame("2222")

        assertNotEquals(gameId1, gameId2)
    }

    should("return correct game id") {
        val gameInfoCaptor = slot<GameInfoEvent>()
        every { msgSender.sendGameStartedMsg(any(), capture(gameInfoCaptor)) } returns Unit

        val gameId = startGame("888")

        assertNotEquals("", gameId)
        assertEquals(gameId, gameInfoCaptor.captured.gameId)
    }

    should("discard session id if session closed") {
        val gameId = startGame("999")
        gameCommandHandler.connectionClosed("999")

        eventSender.gameFinished(gameId, GameResultDto("", ""))

        verify(exactly = 0) { msgSender.sendGameFinishedMsg("999", any()) }
    }

    should("send waiting for player event with game id") {
        val gameId = gameCommandHandler.requestNewGame("1111", StartGameRequest("vs_friend", "white"))

        verify { msgSender.sendWaitingForOtherPlayerMsg("1111", gameId) }
    }

    should("send game started event when second player joins") {
        val gameId = gameCommandHandler.requestNewGame("1111", StartGameRequest("vs_friend", "white"))

        gameCommandHandler.joinGame("2222", gameId)

        verify { msgSender.sendGameStartedMsg("1111", any()) }
        verify { msgSender.sendGameStartedMsg("2222", any()) }
    }
})
