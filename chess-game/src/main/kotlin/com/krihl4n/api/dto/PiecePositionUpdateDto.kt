package com.krihl4n.api.dto

import com.krihl4n.model.PiecePositionUpdate

data class PiecePositionUpdateDto(
    val primaryMove: PerformedMoveDto,
    val secondaryMove: PerformedMoveDto? = null,
    val pieceCapture: PieceCaptureDto? = null,
    val pawnPromotion: String? = null,
    val reverted: Boolean = false,
    val label: String,
    val turn: String,
    val score: ScoreDto
) {

    companion object {
        internal fun from(positionUpdate: PiecePositionUpdate, turn: String, score: ScoreDto): PiecePositionUpdateDto {
            return PiecePositionUpdateDto(
                primaryMove = PerformedMoveDto(
                    positionUpdate.primaryMove.from.token(),
                    positionUpdate.primaryMove.to.token()
                ),
                secondaryMove = positionUpdate.secondaryMove?.let {
                    PerformedMoveDto(
                        positionUpdate.secondaryMove.from.token(),
                        positionUpdate.secondaryMove.to.token()
                    )
                },
                pieceCapture = positionUpdate.pieceCapture?.let {
                    PieceCaptureDto(
                        field = positionUpdate.pieceCapture.field.token(),
                        capturedPiece = PieceDto(
                            positionUpdate.pieceCapture.piece.color.toString(),
                            positionUpdate.pieceCapture.piece.type.toString()
                        )
                    )
                },
                pawnPromotion = positionUpdate.pawnPromotion?.toString(),
                reverted = positionUpdate.reverted,
                label = positionUpdate.label,
                turn = turn,
                score = ScoreDto(score.white, score.black)
            )
        }
    }
}

data class PerformedMoveDto(val from: String, val to: String)

data class PieceCaptureDto(val field: String, val capturedPiece: PieceDto)
