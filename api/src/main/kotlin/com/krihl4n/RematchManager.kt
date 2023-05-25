package com.krihl4n

import com.krihl4n.model.Color
import org.springframework.stereotype.Service

@Service
class RematchManager {

    private val proposals = mutableSetOf<RematchProposal>()

    fun createProposal(player1Id: String, player2Id: String, player1NextColor: Color) {
        proposals.add(RematchProposal(player1Id, player1NextColor))
        if(player1Id != player2Id) {
            proposals.add(RematchProposal(player2Id, player1NextColor.opposite()))
        }
    }

    fun popRematchProposal(playerId: String): RematchProposal? {
        val proposal =  proposals.firstOrNull { it.playerId == playerId }
        proposal?.let { this.proposals.remove(proposal) } // todo - computer entries are never cleared
        return proposal
    }
}

class RematchProposal(val playerId: String, val playerNextColor: Color)