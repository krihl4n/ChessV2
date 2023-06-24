package com.krihl4n

import org.springframework.stereotype.Service

@Service
class RematchProposals {

    private val proposals = mutableSetOf<RematchProposal>()

    fun createProposal(rematch: RematchDto) {
        rematch.players.forEach{
            val proposal = RematchProposal(it.id, it.color, rematch.gameOfChess.gameId, rematch.previousGameId)
            proposals.add(proposal)
        }
    }

    fun getRematchProposal(playerId: String): RematchProposal? {
        return proposals.firstOrNull { it.playerId == playerId }
    }

    fun clearProposals(gameId: String) {
        this.proposals.removeIf { it.gameId == gameId }
    }

    fun proposalExists(previousGameId: String) = this.proposals.find { it.previousGameId == previousGameId } != null
}

data class RematchProposal(val playerId: String, val playerNextColor: String, val gameId: String, val previousGameId: String)