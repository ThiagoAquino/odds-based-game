package odds.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.intellij.lang.annotations.Pattern

data class CreatePlayerRequest(

    @field:NotBlank(message = "Name cannot be blank")
    val name: String,
    @field:NotBlank(message = "Surname cannot be blank")
    val surname: String,
    @field:NotBlank(message = "Username cannot be blank")
    @field:Size(min = 3, max = 32, message = "Username must be between 3 and 32 characters")
    val username: String,
)