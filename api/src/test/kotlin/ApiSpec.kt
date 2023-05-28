import com.krihl4n.GameCommandHandler
import com.krihl4n.GameEventHandler
import com.krihl4n.GamesRegister
import com.krihl4n.RematchManager
import com.krihl4n.app.MessageSender
import com.krihl4n.events.GameInfoEvent
import io.kotest.core.spec.AfterTest
import io.kotest.core.spec.BeforeTest
import io.mockk.*

const val SESSION_ID_1 = "1111"
const val SESSION_ID_2 = "2222"

const val WHITE = "white"
const val BLACK = "black"

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

fun gameStartedCaptor(sessionId: String? = null): CapturingSlot<GameInfoEvent> {
    val gameInfoCaptor = slot<GameInfoEvent>()
    every { msgSender.sendGameStartedMsg(sessionId ?: any(), capture(gameInfoCaptor)) } returns Unit
    return gameInfoCaptor
}
