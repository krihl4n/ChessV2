import com.krihl4n.GameCommandHandler
import com.krihl4n.GameEventHandler
import com.krihl4n.GamesRegister
import com.krihl4n.app.MessageSender
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.clearAllMocks
import io.mockk.mockk

class RematchSpec : ShouldSpec({

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

    // reuse join-game ?

    should("not allow rematch if game is not finished") {

    }

    should("allow first player to propose rematch") {

    }

    should("start new game after second player accepts rematch proposal") {

    }
})