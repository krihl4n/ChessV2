package com.krihl4n.computerOpponent

import com.krihl4n.gamesManagement.GameOfChessCreator
import com.krihl4n.api.GameEventListener
import com.krihl4n.api.dto.*
import com.krihl4n.persistence.GamesRepository
import com.krihl4n.persistence.ReadOnlyGameOfChess
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.util.*
import kotlin.concurrent.schedule

import kotlin.random.Random

@Service
class ComputerOpponent(
    private val gameOfChessCreator: GameOfChessCreator,
    private val gamesRepository: GamesRepository
) : GameEventListener {

    @PostConstruct
    fun post() {
        gameOfChessCreator.registerNewGameObserver(this)
    }

    override fun piecePositionUpdate(gameId: String, update: PiecePositionUpdateDto) {
        if (isVsComputer(gameId) && isCpuTurn(gameId) && !update.reverted) {
            Timer("ScheduleMove", false).schedule(1000) {
                val attacked = attackIfPossible(gameId, cpuColor(gameId))
                if (!attacked) {
                    performRandomMove(gameId, cpuColor(gameId))
                }
            }
        }
    }

    override fun gameStarted(gameId: String, gameInfo: GameInfoDto) {
        if (isVsComputer(gameId) && isCpuTurn(gameId)) {
            Timer("ScheduleMove", false).schedule(1000) {
                performRandomMove(gameId, cpuColor(gameId))
            }
        }
    }

    override fun gameFinished(gameId: String, result: GameResultDto) {
    }

    override fun waitingForOtherPlayer(gameId: String) {
        if (isVsComputer(gameId)) {
            gamesRepository.getGameForCommand(gameId).playerReady("cpu", null)
        }
    }

    private fun isVsComputer(gameId: String) = gamesRepository.getGameForQuery(gameId).gameMode() == "vs_computer"

    private fun isCpuTurn(gameId: String): Boolean {
        val game = gamesRepository.getGameForQuery(gameId)
        val nextColor = game.getColorAllowedToMove()
        val cpuColor = game.getPlayers().first { it.id == "cpu" }.color
        return nextColor == cpuColor
    }

    private fun cpuColor(gameId: String): String {
        val game = gamesRepository.getGameForQuery(gameId)
        return game.getPlayers().first { it.id == "cpu" }.color
    }

    private fun performRandomMove(gameId: String, playerColor: String) {
        val game = gamesRepository.getGameForQuery(gameId)
        val positions = getPositionsOfPiecesOfColor(game, playerColor)
        while (positions.isNotEmpty()) {
            val field = getRandomField(positions)
            val possibleMoves = getPossibleMovesFrom(game, field)
            if (noMovesAvailable(possibleMoves)) {
                positions.remove(field)
                continue
            } else {
                val dst = possibleMoves.to[Random.nextInt(0, possibleMoves.to.size)]
                val promotion = getPromotion(game, field, dst)
                gamesRepository.getGameForCommand(game.gameId())
                    .move(MoveDto("cpu", field, dst, promotion))
                break
            }
        }
    }

    private fun getPromotion(game: ReadOnlyGameOfChess, from: String, dst: String): String? {
        val piece = game.getFieldOccupationInfo().first { it.field == from }.piece
        return if (piece?.type == "PAWN" && (dst[1] == '1' || dst[1] == '8') ) {
            "queen"
        } else {
            null
        }
    }

    private fun getPositionsOfPiecesOfColor(game: ReadOnlyGameOfChess, playerColor: String): MutableList<String> =
        game.getFieldOccupationInfo()
            .filter { it.piece != null && it.piece!!.color.lowercase() == playerColor.lowercase() }
            .map { it.field }
            .toMutableList()

    private fun getRandomField(playersPiecePositions: MutableList<String>) =
        playersPiecePositions[Random.nextInt(0, playersPiecePositions.size)]

    private fun getPossibleMovesFrom(game: ReadOnlyGameOfChess, randomField: String) = game.getPossibleMoves(randomField)

    private fun noMovesAvailable(possibleMoves: PossibleMovesDto) = possibleMoves.to.isEmpty()

    private fun attackIfPossible(gameId: String, playerColor: String): Boolean {
        val game = gamesRepository.getGameForQuery(gameId)
        val positions = getPositionsOfPiecesOfColor(game, playerColor)
        val opponentPositions = getPositionsOfPiecesOfColor(game, oppositeColorOf(playerColor))
        while (positions.isNotEmpty()) {
            val field = getRandomField(positions)
            val possibleMoves = getPossibleMovesFrom(game, field)
            if (noMovesAvailable(possibleMoves)) {
                positions.remove(field)
                continue
            } else {
                for (dst in possibleMoves.to) {
                    if (opponentPositions.contains(dst)) {
                        val promotion = getPromotion(game, field, dst)
                        gamesRepository
                            .getGameForCommand(game.gameId())
                            .move(MoveDto("cpu", field, dst, promotion))
                        return true
                    }
                }
                positions.remove(field)
            }
        }
        return false
    }

    private fun oppositeColorOf(color: String): String {
        return if (color.lowercase() == "white") "black" else "white"
    }
}