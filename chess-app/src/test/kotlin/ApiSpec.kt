import com.krihl4n.*
import com.krihl4n.app.MessageSender
import com.krihl4n.computerOpponent.ComputerOpponent
import com.krihl4n.messages.GameInfoEvent
import com.krihl4n.persistence.GamesRepository
import com.krihl4n.persistence.MongoGamesRepository
import io.kotest.core.spec.AfterTest
import io.kotest.core.spec.BeforeTest
import io.mockk.*
import java.util.*

const val SESSION_ID_1 = "1111"
const val SESSION_ID_2 = "2222"

const val WHITE = "white"
const val BLACK = "black"

const val TEST_MODE = "test_mode"
const val VS_COMPUTER = "vs_computer"
const val VS_FRIEND = "vs_friend"

val msgSender = mockk<MessageSender>(relaxed = true)
val repo = mockk<MongoGamesRepository>(relaxed = false)
var gameOfChessCreator = GameOfChessCreator()
var sessionRegistry = SessionRegistry()
var gamesRepository = GamesRepository(repo, gameOfChessCreator)
var rematchProposals = RematchProposals()
var eventhandler = GameEventHandler(msgSender, sessionRegistry, rematchProposals, gameOfChessCreator)
var gameCommandHandler = GameCommandHandler(sessionRegistry, rematchProposals, msgSender, gameOfChessCreator, gamesRepository)

val beforeApiTest: BeforeTest = {
    gameOfChessCreator = GameOfChessCreator()
    sessionRegistry = SessionRegistry()
    gamesRepository = GamesRepository(repo, gameOfChessCreator)
    rematchProposals = RematchProposals()
    eventhandler = GameEventHandler(msgSender, sessionRegistry, rematchProposals, gameOfChessCreator)
    gameCommandHandler = GameCommandHandler(sessionRegistry, rematchProposals, msgSender, gameOfChessCreator, gamesRepository)
    every { repo.save(any()) }.returnsArgument(0)
    every { repo.findById(any()) }.returns(Optional.empty())
    gameOfChessCreator.registerNewGameObserver(eventhandler)
    gameOfChessCreator.registerNewGameObserver(ComputerOpponent(gameOfChessCreator, gamesRepository))
}

val afterApiTest: AfterTest = { clearAllMocks() }

fun gameStartedCaptor(sessionId: String? = null): CapturingSlot<GameInfoEvent> {
    val gameInfoCaptor = slot<GameInfoEvent>()
    every { msgSender.sendGameStartedMsg(sessionId ?: any(), capture(gameInfoCaptor)) } returns Unit
    return gameInfoCaptor
}
