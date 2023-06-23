package com.krihl4n.computerOpponent

import com.krihl4n.GamesRegistry
import com.krihl4n.api.GameEventListener
import com.krihl4n.api.GameOfChess
import com.krihl4n.api.dto.*
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.util.*
import kotlin.concurrent.schedule

import kotlin.random.Random

@Service
class ComputerOpponent(private val gamesRegistry: GamesRegistry): GameEventListener {

    @PostConstruct
    fun post() {
        gamesRegistry.observeGames(this)
    }

    override fun piecePositionUpdate(gameId: String, update: PiecePositionUpdateDto) {
        println("CPU  $gameId --> piecePositionUpdate")
        if (isVsComputer(gameId) && isCpuTurn(gameId)) {
            Timer("ScheduleMove", false).schedule(1000) {
                val attacked = attackIfPossible(gameId, cpuColor(gameId))
                if(!attacked) {
                    performRandomMove(gameId, cpuColor(gameId))
                }
            }
        }
    }

    override fun gameStateUpdate(gameId: String, update: GameStateUpdateDto) {
        println("CPU $gameId  --> gameStateUpdate")
    }

    override fun gameStarted(gameId: String, gameInfo: GameInfoDto) {
        println("CPU $gameId --> gameStarted")
        if (isVsComputer(gameId) && isCpuTurn(gameId)) {
            Timer("ScheduleMove", false).schedule(1000) {
                performRandomMove(gameId, cpuColor(gameId))
            }
        }
    }

    override fun gameFinished(gameId: String, result: GameResultDto) {
        println("CPU $gameId  --> gameFinished")
    }

    override fun waitingForOtherPlayer(gameId: String) {
        println("CPU $gameId  :: waitingForOtherPlayer")
        if (isVsComputer(gameId)) {
            gamesRegistry.getGameForCommand(gameId)?.playerReady("cpu", null)
        }
    }

    private fun isVsComputer(gameId: String) = gamesRegistry.getGameForQueryById(gameId).gameMode == "vs_computer"

    private fun isCpuTurn(gameId: String): Boolean {
        val game = gamesRegistry.getGameForQueryById(gameId)
        val nextColor =  game.getColorAllowedToMove()
        val cpuColor = game.getPlayers().first { it.id == "cpu" }.color
        return nextColor == cpuColor
    }

    private fun cpuColor(gameId: String): String {
        val game = gamesRegistry.getGameForQueryById(gameId)
        return game.getPlayers().first { it.id == "cpu" }.color
    }

    private fun performRandomMove(gameId: String, playerColor: String) {
        val game = gamesRegistry.getGameForQueryById(gameId)
        val positions = getPositionsOfPiecesOfColor(game, playerColor)
        while (positions.isNotEmpty()) {
            val field = getRandomField(positions)
            val possibleMoves = getPossibleMovesFrom(game, field)
            if (noMovesAvailable(possibleMoves)) {
                positions.remove(field)
                continue
            } else {
                gamesRegistry.getGameForCommand(game.gameId)
                    ?.move(MoveDto("cpu", field, possibleMoves.to[Random.nextInt(0, possibleMoves.to.size)], "queen"))
                break
            }
        }
    }

    private fun getPositionsOfPiecesOfColor(game: GameOfChess, playerColor: String): MutableList<String> =
        game.getFieldOccupationInfo()
        .filter { it.piece != null && it.piece!!.color.lowercase() == playerColor.lowercase() }
        .map { it.field }
        .toMutableList()

    private fun getRandomField(playersPiecePositions: MutableList<String>) =
        playersPiecePositions[Random.nextInt(0, playersPiecePositions.size)]

    private fun getPossibleMovesFrom(game: GameOfChess, randomField: String) = game.getPossibleMoves(randomField)

    private fun noMovesAvailable(possibleMoves: PossibleMovesDto) = possibleMoves.to.isEmpty()

    private fun attackIfPossible(gameId: String, playerColor: String): Boolean{
        val game = gamesRegistry.getGameForQueryById(gameId)
        val positions = getPositionsOfPiecesOfColor(game, playerColor)
        val opponentPositions = getPositionsOfPiecesOfColor(game, oppositeColorOf(playerColor))
        while (positions.isNotEmpty()) {
            val field = getRandomField(positions)
            val possibleMoves = getPossibleMovesFrom(game, field)
            if (noMovesAvailable(possibleMoves)) {
                positions.remove(field)
                continue
            } else {
                for(dst in possibleMoves.to) {
                    if(opponentPositions.contains(dst)) {
                        gamesRegistry.getGameForCommand(game.gameId)
                            ?.move(MoveDto("cpu", field, dst, "queen"))
                        return true
                    }
                }
                positions.remove(field)
            }
        }
        return false
    }

    private fun oppositeColorOf(color: String): String {
        return if(color.lowercase() == "white") "black" else "white"
    }
}