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

const val VS_COMPUTER = "vs_computer"
const val VS_FRIEND = "vs_friend"

val msgSender = mockk<MessageSender>(relaxed = true)
var gamesRegister = GamesRegister()
var rematchManager = RematchManager()
var eventhandler = GameEventHandler(msgSender, gamesRegister, rematchManager)
var gameCommandHandler = GameCommandHandler(eventhandler, gamesRegister, rematchManager, msgSender)

val beforeApiTest: BeforeTest = {
    gamesRegister = GamesRegister()
    rematchManager = RematchManager()
    eventhandler = GameEventHandler(msgSender, gamesRegister, rematchManager)
    gameCommandHandler = GameCommandHandler(eventhandler, gamesRegister, rematchManager, msgSender)
}

val afterApiTest: AfterTest = { clearAllMocks() }

fun gameStartedCaptor(sessionId: String? = null): CapturingSlot<GameInfoEvent> {
    val gameInfoCaptor = slot<GameInfoEvent>()
    every { msgSender.sendGameStartedMsg(sessionId ?: any(), capture(gameInfoCaptor)) } returns Unit
    return gameInfoCaptor
}