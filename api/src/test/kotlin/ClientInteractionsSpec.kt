import com.krihl4n.*
import com.krihl4n.api.dto.GameInfoDto
import com.krihl4n.api.dto.PlayerDto
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals

class ClientInteractionsSpec : ShouldSpec({
    val msgSender = mockk<MessageSender>(relaxed = true)
    var gamesRegister = GamesRegister()
    var gameHandler = GameHandler(GameEventSender(msgSender, gamesRegister), gamesRegister)

    beforeTest {
        gamesRegister = GamesRegister()
        gameHandler = GameHandler(GameEventSender(msgSender, gamesRegister), gamesRegister)
    }

    fun startGame(sessionId: String = "1111"): String {
        return gameHandler.requestNewGame(sessionId, StartGameRequest("player1", "vs_computer", "white"))
    }

    should("notify player 1 that game has started when playing vs computer") {
        val gameInfoCaptor = slot<GameInfoDto>()
        every { msgSender.sendGameStartedMsg(any(), capture(gameInfoCaptor)) } returns Unit

        val gameId = startGame("999")

        verify { msgSender.sendGameStartedMsg("999", any()) }
        assertNotEquals("999", gameInfoCaptor.captured.gameId)
        assertEquals(gameId, gameInfoCaptor.captured.gameId)
        assertEquals("VS_COMPUTER", gameInfoCaptor.captured.mode)
        assertEquals(PlayerDto("player1", "WHITE"), gameInfoCaptor.captured.player1)
        assertEquals(PlayerDto("computer", "BLACK"), gameInfoCaptor.captured.player2)
    }

    should("game ids be unique") {
        val gameId1 = startGame("1111")
        val gameId2 = startGame("2222")

        assertNotEquals(gameId1, gameId2)
    }

    should("return correct game id") {
        val gameInfoCaptor = slot<GameInfoDto>()
        every { msgSender.sendGameStartedMsg(any(), capture(gameInfoCaptor)) } returns Unit

        val gameId = startGame("888")

        assertNotEquals("", gameId)
        assertEquals(gameId, gameInfoCaptor.captured.gameId)
    }
})
