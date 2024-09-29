package odds.repositories

import odds.domain.Player
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono


@Repository
interface PlayerRepository : ReactiveCrudRepository<Player, Long> {
    fun findByUsername(username: String): Mono<Player>
}