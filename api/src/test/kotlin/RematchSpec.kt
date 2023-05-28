import com.krihl4n.requests.JoinGameRequest
import com.krihl4n.requests.StartGameRequest
import io.kotest.core.spec.style.FunSpec
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull

class RematchSpec : FunSpec({

    beforeTest(beforeApiTest)
    afterTest(afterApiTest)

    test("should initialize a new game") {
        val gameId = gameCommandHandler.requestNewGame(SESSION_ID_1, StartGameRequest("", null))
        gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(gameId))

        val newGameId = gameCommandHandler.requestRematch(SESSION_ID_1)

        assertNotNull(gamesRegister.getGame(SESSION_ID_1))
        assertEquals(newGameId, gamesRegister.getGame(SESSION_ID_1)?.gameId)
    }

    test("should start a new game") {
        val gameId = gameCommandHandler.requestNewGame(SESSION_ID_1, StartGameRequest("", null))
        gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(gameId))
        val gameInfoCaptor = gameStartedCaptor()

        val newGameId = gameCommandHandler.requestRematch(SESSION_ID_1)
        gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(newGameId))

        verify { msgSender.sendGameStartedMsg(SESSION_ID_1, any()) }
        assertEquals(newGameId, gameInfoCaptor.captured.gameId)
    }

    test("player should remain his id") {
        val gameInfoCaptor = gameStartedCaptor()
        val gameId = gameCommandHandler.requestNewGame(SESSION_ID_1, StartGameRequest("", null))
        gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(gameId))
        val originalGamePlayer = gameInfoCaptor.captured.player

        gameCommandHandler.requestRematch(SESSION_ID_1)

        verify { msgSender.sendGameStartedMsg(SESSION_ID_1, any()) }
        assertEquals(originalGamePlayer.id, gameInfoCaptor.captured.player.id)
    }

    test("game mode should remain unchanged") {
        val gameId = gameCommandHandler.requestNewGame(SESSION_ID_1, StartGameRequest(VS_COMPUTER, null))
        gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(gameId))
        val gameInfoCaptor = gameStartedCaptor()

        val newGameId = gameCommandHandler.requestRematch(SESSION_ID_1)
        gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(newGameId))

        verify { msgSender.sendGameStartedMsg(SESSION_ID_1, any()) }
        assertEquals(VS_COMPUTER, gameInfoCaptor.captured.mode)
    }

    // should notify all players about rematch

//    test("should not allow rematch if game is not finished") {
//
//    }
//
// two players propose rematch at the same time
})
