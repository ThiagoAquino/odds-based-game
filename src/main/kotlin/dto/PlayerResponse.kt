package dto

data class PlayerResponse(
    val id: Long?,
    val name: String,
    val surname: String,
    val username: String,
    var balance: Double
)