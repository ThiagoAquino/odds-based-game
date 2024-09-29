package odds.domain

import dto.enum.ResultEnum
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("bet")
data class Bet(

    @Id
    @Column("id")
    val id: Long = 0,

    @Column("player_id")
    val playerId: Long, // Foreign key that relates to the Player entity

    @Column("bet_amount")
    val betAmount: Double,

    @Column("bet_number")
    val betNumber: Int,

    @Column("generated_number")
    val generatedNumber: Int,

    @Column("bet_result")
    val result: ResultEnum,

    @Column("bet_winning")
    val winnings: Double
)