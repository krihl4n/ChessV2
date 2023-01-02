import com.krihl4n.*
import com.krihl4n.app.MessageSender
import com.krihl4n.requests.JoinAsPlayerTwoRequest
import com.krihl4n.requests.StartGameRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.*

class JoinPlayerTwoSpec : ShouldSpec({
    val msgSender = mockk<MessageSender>(relaxed = true)
    var gamesRegister = GamesRegister()
    var joinGameHandler = JoinGameHandler()
    var eventSender = GameEventHandler(msgSender, gamesRegister, joinGameHandler)
    var gameHandler = GameHandler(eventSender, gamesRegister, joinGameHandler)

    beforeTest {
        gamesRegister = GamesRegister()
        joinGameHandler = JoinGameHandler()
        eventSender = GameEventHandler(msgSender, gamesRegister, joinGameHandler)
        gameHandler = GameHandler(eventSender, gamesRegister, joinGameHandler)
    }

    should("send waiting for player event with join code") {
        val joinCodeCaptor = slot<String>()
        every { msgSender.sendWaitingForOtherPlayerMsg(any(), capture(joinCodeCaptor)) } returns Unit

        gameHandler.requestNewGame("1111", StartGameRequest("player1", "vs_friend", "white"))

        verify { msgSender.sendWaitingForOtherPlayerMsg("1111", joinCodeCaptor.captured) }
    }

    should("send start game event when second player joins") {
        val joinCodeCaptor = slot<String>()
        every { msgSender.sendWaitingForOtherPlayerMsg(any(), capture(joinCodeCaptor)) } returns Unit
        gameHandler.requestNewGame("1111", StartGameRequest("player1", "vs_friend", "white"))

        gameHandler.joinAsPlayerTwo("2222", JoinAsPlayerTwoRequest("player2", joinCodeCaptor.captured))

        verify { msgSender.sendGameStartedMsg("1111", any()) }
        verify { msgSender.sendGameStartedMsg("2222", any()) }
    }

    should("reject join attempt if join code incorrect") {
        val joinCodeCaptor = slot<String>()
        every { msgSender.sendWaitingForOtherPlayerMsg(any(), capture(joinCodeCaptor)) } returns Unit
        gameHandler.requestNewGame("1111", StartGameRequest("player1", "vs_friend", "white"))

        shouldThrow<RuntimeException> {
            gameHandler.joinAsPlayerTwo("2222", JoinAsPlayerTwoRequest("player2", "incorrect-code"))
        }
    }

    should("not be able to reuse join code") {
        val joinCodeCaptor = slot<String>()
        every { msgSender.sendWaitingForOtherPlayerMsg(any(), capture(joinCodeCaptor)) } returns Unit
        gameHandler.requestNewGame("1111", StartGameRequest("player1", "vs_friend", "white"))
        gameHandler.joinAsPlayerTwo("2222", JoinAsPlayerTwoRequest("player2", joinCodeCaptor.captured))

        shouldThrow<RuntimeException> {
            gameHandler.joinAsPlayerTwo("2222", JoinAsPlayerTwoRequest("player2", joinCodeCaptor.captured))
        }
    }
})
