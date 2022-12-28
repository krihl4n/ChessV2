import com.krihl4n.GameEventSender
import com.krihl4n.GameHandler
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldNotBe
import io.mockk.mockk

class ClientInteractionsSpec : ShouldSpec({
    val eventSender = mockk<GameEventSender>()
    val gameHandler = GameHandler(eventSender)

    should("should run") {
        gameHandler shouldNotBe(null)
    }
})