import com.krihl4n.GameCommandHandler
import com.krihl4n.GameEventHandler
import com.krihl4n.GamesRegister
import com.krihl4n.RematchManager
import com.krihl4n.app.MessageSender
import io.kotest.core.spec.AfterTest
import io.kotest.core.spec.BeforeTest
import io.mockk.clearAllMocks
import io.mockk.mockk

val msgSender = mockk<MessageSender>(relaxed = true)
var gamesRegister = GamesRegister()
var eventSender = GameEventHandler(msgSender, gamesRegister)
var rematchManager = RematchManager()
var gameCommandHandler = GameCommandHandler(eventSender, gamesRegister, rematchManager)

val beforeApiTest: BeforeTest = {
    gamesRegister = GamesRegister()
    eventSender = GameEventHandler(msgSender, gamesRegister)
    rematchManager = RematchManager()
    gameCommandHandler = GameCommandHandler(eventSender, gamesRegister, rematchManager)
}

val afterApiTest: AfterTest = { clearAllMocks() }
