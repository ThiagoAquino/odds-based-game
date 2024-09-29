package odds.dto

import dto.enum.TransactionTypeEnum
import jakarta.validation.constraints.NotBlank

data class CreateTransactionDTO (
    @field:NotBlank(message = "Name cannot be blank")
    val playerUsername: String,
    val amount: Double,
    val type: TransactionTypeEnum = TransactionTypeEnum.DEPOSIT
)