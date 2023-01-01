import com.krihl4n.*
import com.krihl4n.app.MessageSender
import com.krihl4n.requests.StartGameRequest
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.*

class JoinPlayerTwoSpec : ShouldSpec({
    val msgSender = mockk<MessageSender>(relaxed = true)
    var gamesRegister = GamesRegister()
    var eventSender = GameEventHandler(msgSender, gamesRegister)
    var gameHandler = GameHandler(eventSender, gamesRegister)

    beforeTest {
        gamesRegister = GamesRegister()
        eventSender = GameEventHandler(msgSender, gamesRegister)
        gameHandler = GameHandler(eventSender, gamesRegister)
    }

    should("send waiting for player event with join code") {
        every { msgSender.sendWaitingForOtherPlayerMsg("1111", any()) } returns Unit

        gameHandler.requestNewGame("1111", StartGameRequest("player1", "vs_friend", "white"))

        verify { msgSender.sendWaitingForOtherPlayerMsg("1111", "7777") }
    }
})
