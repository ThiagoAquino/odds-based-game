package odds.dto

import dto.enum.TransactionTypeEnum

data class TransactionResponse(
    val id: Long?,
    val playerUsername: String,
    val amount: Double,
    val type: TransactionTypeEnum
)