import com.krihl4n.app.messages.JoinGameRequest
import com.krihl4n.app.messages.StartGameRequest
import io.kotest.core.spec.style.FunSpec
import io.mockk.*
import org.junit.jupiter.api.Assertions.*

class RematchSpec : FunSpec({

    beforeTest(beforeApiTest)
    afterTest(afterApiTest)

    test("should initialize a new game") {
        val gameId = gameCommandHandler.requestNewGame(SESSION_ID_1, StartGameRequest(TEST_MODE, null))
        gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(gameId))

        val newGameId = gameCommandHandler.requestRematch(SESSION_ID_1, gameId)

        assertNotNull(gamesRepository.getGameForQuery(newGameId!!))
    }

    test("should start a new game") {
        val gameId = gameCommandHandler.requestNewGame(SESSION_ID_1, StartGameRequest(TEST_MODE, null))
        gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(gameId))
        val gameInfoCaptor = gameStartedCaptor()

        val newGameId = gameCommandHandler.requestRematch(SESSION_ID_1, gameId)
        gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(newGameId!!))

        verify { msgSender.sendGameStartedMsg(SESSION_ID_1, any()) }
        assertEquals(newGameId, gameInfoCaptor.captured.gameId)
    }

    test("should start a new game when friend joins") {
        val gameId = gameCommandHandler.requestNewGame(SESSION_ID_1, StartGameRequest(VS_FRIEND, null))
        gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(gameId))
        gameCommandHandler.joinGame(SESSION_ID_2, JoinGameRequest(gameId))
        val gameInfoCaptor = gameStartedCaptor()

        val newGameId = gameCommandHandler.requestRematch(SESSION_ID_1, gameId)
        gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(newGameId!!))
        gameCommandHandler.joinGame(SESSION_ID_2, JoinGameRequest(newGameId))

        verify { msgSender.sendGameStartedMsg(SESSION_ID_1, any()) }
        verify { msgSender.sendGameStartedMsg(SESSION_ID_2, any()) }
        assertEquals(newGameId, gameInfoCaptor.captured.gameId)
    }

    test("player should remain his id") {
        val gameInfoCaptor = gameStartedCaptor()
        val gameId = gameCommandHandler.requestNewGame(SESSION_ID_1, StartGameRequest(TEST_MODE, null))
        gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(gameId))
        val originalGamePlayer = gameInfoCaptor.captured.player

        gameCommandHandler.requestRematch(SESSION_ID_1, gameId)

        verify { msgSender.sendGameStartedMsg(SESSION_ID_1, any()) }
        assertEquals(originalGamePlayer.id, gameInfoCaptor.captured.player.id)
    }

    test("game mode should remain unchanged") {
        val gameId = gameCommandHandler.requestNewGame(SESSION_ID_1, StartGameRequest(VS_COMPUTER, null))
        gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(gameId))
        val gameInfoCaptor = gameStartedCaptor()

        val newGameId = gameCommandHandler.requestRematch(SESSION_ID_1, gameId)
        gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(newGameId!!))

        verify { msgSender.sendGameStartedMsg(SESSION_ID_1, any()) }
        assertEquals(VS_COMPUTER, gameInfoCaptor.captured.mode)
    }

    test("players should change colors for rematch") {
        val gameId = gameCommandHandler.requestNewGame(SESSION_ID_1, StartGameRequest(VS_FRIEND, null))
        gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(gameId, WHITE, playerId = "p1"))
        gameCommandHandler.joinGame(SESSION_ID_2, JoinGameRequest(gameId, playerId = "p2"))
        val gameInfoCaptorP1 = gameStartedCaptor(SESSION_ID_1)
        val gameInfoCaptorP2 = gameStartedCaptor(SESSION_ID_2)

        val newGameId = gameCommandHandler.requestRematch(SESSION_ID_1, gameId)
        gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(newGameId!!, playerId = "p1"))
        gameCommandHandler.joinGame(SESSION_ID_2, JoinGameRequest(newGameId, playerId = "p2"))

        assertEquals(BLACK, gameInfoCaptorP1.captured.player.color)
        assertEquals(WHITE, gameInfoCaptorP2.captured.player.color)
    }

    test("should not generate 2 games if players propose rematch at the same time") {
        val gameId = gameCommandHandler.requestNewGame(SESSION_ID_1, StartGameRequest(VS_FRIEND, null))
        gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(gameId, WHITE, playerId = "p1"))
        gameCommandHandler.joinGame(SESSION_ID_2, JoinGameRequest(gameId, playerId = "p2"))

        val newGameId1 = gameCommandHandler.requestRematch(SESSION_ID_1, gameId)
        val newGameId2 = gameCommandHandler.requestRematch(SESSION_ID_2, gameId)

        assertNotNull(newGameId1)
        assertNull(newGameId2)
        verify(exactly = 1) { msgSender.sendRematchRequestedMsg(SESSION_ID_2, any()) }
        verify(exactly = 0) { msgSender.sendRematchRequestedMsg(SESSION_ID_1, any()) }
    }

    test("proposals should be cleared when game is started") {
        val gameId = gameCommandHandler.requestNewGame(SESSION_ID_1, StartGameRequest(VS_FRIEND, null))
        gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(gameId, WHITE, playerId = "p1"))
        gameCommandHandler.joinGame(SESSION_ID_2, JoinGameRequest(gameId, playerId = "p2"))

        val newGameId = gameCommandHandler.requestRematch(SESSION_ID_1, gameId)
        gameCommandHandler.joinGame(SESSION_ID_1, JoinGameRequest(newGameId!!, playerId = "p1"))
        gameCommandHandler.joinGame(SESSION_ID_2, JoinGameRequest(newGameId, playerId = "p2"))

        assertNull(rematchProposals.getRematchProposal("p1"))
        assertNull(rematchProposals.getRematchProposal("p2"))
    }
})
