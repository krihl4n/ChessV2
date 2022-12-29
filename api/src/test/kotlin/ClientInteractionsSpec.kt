import com.krihl4n.GameEventSender
import com.krihl4n.GameHandler
import com.krihl4n.StartGameRequest
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
    val eventSender = mockk<GameEventSender>(relaxed = true)
    val gameHandler = GameHandler(eventSender)

    fun startGame(sessionId: String = "1111"): String{
        return gameHandler.requestNewGame(sessionId, StartGameRequest("player1", "vs_computer", "white"))
    }

    should("notify player 1 that game has started when playing vs computer") {
        val gameInfoCaptor = slot<GameInfoDto>()
        every { eventSender.gameStarted(any(), capture(gameInfoCaptor)) } returns Unit

        startGame()

        verify { eventSender.gameStarted("1111", any()) }
        assertNotEquals("1111", gameInfoCaptor.captured.gameId)
        assertEquals("VS_COMPUTER", gameInfoCaptor.captured.mode)
        assertEquals(PlayerDto("player1", "WHITE"), gameInfoCaptor.captured.player1)
        assertEquals(PlayerDto("computer", "BLACK"), gameInfoCaptor.captured.player2)
    }

    should("game ids be unique") {
        val gameInfoCaptor1 = slot<GameInfoDto>()
        val gameInfoCaptor2 = slot<GameInfoDto>()
        every { eventSender.gameStarted("1111", capture(gameInfoCaptor1)) } returns Unit
        every { eventSender.gameStarted("2222", capture(gameInfoCaptor2)) } returns Unit

        startGame("1111")
        startGame("2222")

        assertNotEquals(gameInfoCaptor1.captured.gameId, gameInfoCaptor2.captured.gameId)
    }

    should("return correct game id") {
        val gameInfoCaptor = slot<GameInfoDto>()
        every { eventSender.gameStarted("1111", capture(gameInfoCaptor)) } returns Unit
        val gameId = startGame("1111")

        assertNotEquals("", gameId)
        assertEquals(gameId, gameInfoCaptor.captured.gameId)
    }
})
