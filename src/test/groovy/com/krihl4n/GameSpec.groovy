package com.krihl4n

import com.krihl4n.command.CommandCoordinator
import com.krihl4n.command.CommandFactory
import com.krihl4n.model.Field
import com.krihl4n.model.Piece
import spock.lang.Subject

class GameSpec extends BaseSpec {

    @Subject
    Game game
    PositionTracker positionTracker

    Piece piece = aWhitePawn()
    Field from = new Field("a2")
    Field to = new Field("a3")

    void setup() {
        positionTracker = new PositionTracker()
        positionTracker.setPieceAtField(piece, from)
        CommandCoordinator commandCoordinator = new CommandCoordinator()
        CommandFactory commandFactory = new CommandFactory(positionTracker)
        game = new Game(positionTracker, commandCoordinator, commandFactory)
    }

    def "can't perform move if game not started"() {
        when:
        game.performMove(from, to)

        then:
        thrown(IllegalStateException)
    }

    def "can perform move when game is started"() {
        given:
        game.start()

        when:
        def result = game.performMove(from, to)

        then:
        result
    }

    def "can't perform move if game has been finished"() {
        given:
        game.start()
        game.finish()

        when:
        game.performMove(from, to)

        then:
        thrown(IllegalStateException)
    }

    def "position tracker should be updated when move performed"() {
        given:
        game.start()

        when:
        game.performMove(from, to)

        then:
        positionTracker.getPieceAt(to) == piece
    }

    def "should return false if move couldn't be performed"() {
        given:
        game.start()
        positionTracker.removePieceFromField(from)

        when:
        def result = game.performMove(from, to)

        then:
        !result
    }

    def "should undo move"() {
        given:
        game.start()

        when:
        game.performMove(from, to)
        and:
        game.undoMove()

        then:
        positionTracker.getPieceAt(to) == null
        and:
        positionTracker.getPieceAt(from) == piece
    }

    def "should redo move"() {
        given:
        game.start()

        when:
        game.performMove(from, to)
        and:
        game.undoMove()
        and:
        game.redoMove()

        then:
        positionTracker.getPieceAt(from) == null
        and:
        positionTracker.getPieceAt(to) == piece
    }
}
