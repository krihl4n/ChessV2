package com.krihl4n

import com.krihl4n.api.dto.ColorDto
import org.springframework.stereotype.Service

@Service
class RematchManager {

    private val proposals = mutableSetOf<RematchProposal>()

    fun createProposal(player1Id: String, player2Id: String, player1NextColor: ColorDto, gameId: String) {
        proposals.add(RematchProposal(player1Id, player1NextColor, gameId))
        if (player1Id != player2Id) {
            proposals.add(RematchProposal(player2Id, player1NextColor.opposite(), gameId))
        }
    }

    fun getRematchProposal(playerId: String): RematchProposal? {
        return proposals.firstOrNull { it.playerId == playerId }
    }

    fun clearProposals(gameId: String) {
        this.proposals.removeIf { it.gameId == gameId }
    }
}

data class RematchProposal(val playerId: String, val playerNextColor: ColorDto, val gameId: String)