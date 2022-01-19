package com.krihl4n.api.dto

import com.krihl4n.model.PiecePositionUpdate

data class PiecePositionUpdateDto(val primaryMove: MoveDto, val secondaryMove: MoveDto? = null) {

    companion object {
        internal fun from(positionUpdate: PiecePositionUpdate): PiecePositionUpdateDto {
            return PiecePositionUpdateDto(
                primaryMove = MoveDto(
                    positionUpdate.primaryMove.from.token(),
                    positionUpdate.primaryMove.to.token()),
                secondaryMove = positionUpdate.secondaryMove?.let {
                    MoveDto(
                        positionUpdate.secondaryMove.from.token(),
                        positionUpdate.secondaryMove.to.token()
                    )
                }
            )
        }
    }
}

data class MoveDto(val from: String, val to: String)