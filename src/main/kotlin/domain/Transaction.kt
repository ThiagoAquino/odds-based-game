package odds.domain

import dto.enum.TransactionTypeEnum
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table


@Table("transactions")
data class Transaction(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("player_id")
    val playerId: Long, // Foreign key that relates to the Player entity

    @Column("amount")
    val amount: Double,

    @Column("transaction_type")
    val type: TransactionTypeEnum
)
