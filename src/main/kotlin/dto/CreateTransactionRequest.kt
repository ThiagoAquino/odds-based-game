package odds.dto

import dto.enum.TransactionTypeEnum
import jakarta.validation.constraints.NotBlank

data class CreateTransactionRequest (
    @field:NotBlank(message = "Name cannot be blank")
    val playerUsername: String,
    val amount: Double,
    val type: TransactionTypeEnum = TransactionTypeEnum.DEPOSIT
)