package odds.dto

import jakarta.validation.constraints.NotBlank

class CreateBetRequest(
    @field:NotBlank(message = "Username cannot be blank")
    val username: String,
    val betAmount: Double,
    val betNumber: Int
)