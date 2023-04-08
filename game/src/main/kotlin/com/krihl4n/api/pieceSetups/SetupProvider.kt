package com.krihl4n.api.pieceSetups

object SetupProvider {
    fun getSetup(setupName: String?): PieceSetup? {
        return when (setupName){
            "white-pawn-promotion" -> WhitePawnPromotionSetup()
            "about-to-check-mate" -> AboutToCheckMateSetup()
            "about-to-stalemate" -> AboutToStalemateSetup()
            "black-pawn-promotion" -> BlackPawnPromotionSetup()
            "castling" -> CastlingPieceSetup()
            "enpassant" -> EnPassantSetup()
            "simple-attack" -> SimpleAttackSetup()
            else -> null
        }
    }
}