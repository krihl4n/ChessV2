package com.krihl4n.api.dto

import com.krihl4n.model.PiecePositionUpdate

data class PiecePositionUpdateDto(
    val primaryMove: MoveDto,
    val secondaryMove: MoveDto? = null,
    val pieceCapture: PieceCaptureDto? = null,
    val convertToQueen: Boolean = false,
    val reverted: Boolean = false
) {

    companion object {
        internal fun from(positionUpdate: PiecePositionUpdate): PiecePositionUpdateDto {
            return PiecePositionUpdateDto(
                primaryMove = MoveDto(
                    positionUpdate.primaryMove.from.token(),
                    positionUpdate.primaryMove.to.token()
                ),
                secondaryMove = positionUpdate.secondaryMove?.let {
                    MoveDto(
                        positionUpdate.secondaryMove.from.token(),
                        positionUpdate.secondaryMove.to.token()
                    )
                },
                pieceCapture = positionUpdate.pieceCapture?.let {
                    PieceCaptureDto(
                        field = positionUpdate.pieceCapture.field.token(),
                        capturedPiece = PieceDto(
                            positionUpdate.pieceCapture.piece.color.name,
                            positionUpdate.pieceCapture.piece.type.name
                        )
                    )
                },
                convertToQueen = positionUpdate.convertToQueen,
                reverted = positionUpdate.reverted
            )
        }
    }
}

data class MoveDto(val from: String, val to: String)

data class PieceCaptureDto(val field: String, val capturedPiece: PieceDto)
