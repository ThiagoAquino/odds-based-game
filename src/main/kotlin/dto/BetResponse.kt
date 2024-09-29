package odds.dto

import dto.enum.ResultEnum

data class BetResponse(
    val id: Long?,
    val playerId: Long?,
    val betAmount: Double,
    val betNumber: Int,
    val generatedNumber: Int,
    val result: ResultEnum,
    val winnings: Double
)
