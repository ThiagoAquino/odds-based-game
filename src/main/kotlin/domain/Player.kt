package odds.domain


import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("player")
data class Player (
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("player_name")
    val name: String,

    @Column("player_surname")
    val surname: String,

    @Column("username")
    val username: String,

    @Column("balance")
    var balance: Double = 1000.0
)