import com.krihl4n.GameCommandHandler
import com.krihl4n.GameEventHandler
import com.krihl4n.GamesRegister
import com.krihl4n.RematchManager
import com.krihl4n.app.MessageSender
import com.krihl4n.events.GameInfoEvent
import com.krihl4n.requests.JoinGameRequest
import com.krihl4n.requests.StartGameRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import java.lang.RuntimeException

class RematchSpec : FunSpec({

    val msgSender = mockk<MessageSender>(relaxed = true)
    var gamesRegister = GamesRegister()
    var eventSender = GameEventHandler(msgSender, gamesRegister)
    var rematchManager = RematchManager()
    var gameCommandHandler = GameCommandHandler(eventSender, gamesRegister, rematchManager)

    beforeTest {
        gamesRegister = GamesRegister()
        eventSender = GameEventHandler(msgSender, gamesRegister)
        rematchManager = RematchManager()
        gameCommandHandler = GameCommandHandler(eventSender, gamesRegister, rematchManager)
    }

    afterTest { clearAllMocks() }

    test("should initialize a new game") {
        val gameId = gameCommandHandler.requestNewGame("1111", StartGameRequest("", null))
        gameCommandHandler.joinGame("1111", JoinGameRequest(gameId))

        val newGameId = gameCommandHandler.requestRematch("1111")

        assertNotNull(gamesRegister.getGame("1111"))
        assertEquals(newGameId, gamesRegister.getGame("1111")?.gameId)
    }

    test("should start a new game") {
        val gameId = gameCommandHandler.requestNewGame("1111", StartGameRequest("", null))
        gameCommandHandler.joinGame("1111", JoinGameRequest(gameId))
        val gameInfoCaptor = slot<GameInfoEvent>()
        every { msgSender.sendGameStartedMsg(any(), capture(gameInfoCaptor)) } returns Unit

        val newGameId = gameCommandHandler.requestRematch("1111")
        gameCommandHandler.joinGame("1111", JoinGameRequest(newGameId))

        verify { msgSender.sendGameStartedMsg("1111", any()) }
        assertEquals(newGameId, gameInfoCaptor.captured.gameId)
    }

    test("player should remain his id") {
        val gameInfoCaptor = slot<GameInfoEvent>()
        every { msgSender.sendGameStartedMsg(any(), capture(gameInfoCaptor)) } returns Unit

        val gameId = gameCommandHandler.requestNewGame("1111", StartGameRequest("", null))
        gameCommandHandler.joinGame("1111", JoinGameRequest(gameId))
        val originalGamePlayer = gameInfoCaptor.captured.player

        gameCommandHandler.requestRematch("1111")

        verify { msgSender.sendGameStartedMsg("1111", any()) }
        assertEquals(originalGamePlayer.id, gameInfoCaptor.captured.player.id)
    }

    test("game mode should remain unchanged") {
        val gameId = gameCommandHandler.requestNewGame("1111", StartGameRequest("vs_computer", null))
        gameCommandHandler.joinGame("1111", JoinGameRequest(gameId))
        val gameInfoCaptor = slot<GameInfoEvent>()
        every { msgSender.sendGameStartedMsg(any(), capture(gameInfoCaptor)) } returns Unit

        val newGameId = gameCommandHandler.requestRematch("1111")
        gameCommandHandler.joinGame("1111", JoinGameRequest(newGameId))

        verify { msgSender.sendGameStartedMsg("1111", any()) }
        assertEquals("VS_COMPUTER", gameInfoCaptor.captured.mode)
    }

    // should notify all players about rematch

//    test("should not allow rematch if game is not finished") {
//
//    }
//
// two players propose rematch at the same time
})
