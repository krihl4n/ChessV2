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

    should("notify player 1 that game has started when playing vs computer") {
        val gameInfoCaptor = slot<GameInfoDto>()
        every { eventSender.gameStarted(any(), capture(gameInfoCaptor)) } returns Unit

        gameHandler.requestNewGame("1234", StartGameRequest("player1", "vs_computer", "white"))

        verify{eventSender.gameStarted("1234", any())}
        val capturedGameInfo = gameInfoCaptor.captured
        assertNotEquals("1234", capturedGameInfo.gameId)
        assertEquals("VS_COMPUTER", capturedGameInfo.mode)
        assertEquals(PlayerDto("player1", "WHITE"), capturedGameInfo.player1)
        assertEquals(PlayerDto("computer", "BLACK"), capturedGameInfo.player2)
    }

    should("game ids be unique") {
        val gameInfoCaptor1 = slot<GameInfoDto>()
        val gameInfoCaptor2 = slot<GameInfoDto>()
        every { eventSender.gameStarted("1234", capture(gameInfoCaptor1)) } returns Unit
        every { eventSender.gameStarted("1235", capture(gameInfoCaptor2)) } returns Unit

        gameHandler.requestNewGame("1234", StartGameRequest("player1", "vs_computer", "white"))
        gameHandler.requestNewGame("1235", StartGameRequest("player1", "vs_computer", "white"))

        assertNotEquals(gameInfoCaptor1.captured.gameId, gameInfoCaptor2.captured.gameId)
    }
})