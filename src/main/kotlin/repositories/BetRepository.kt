package odds.repositories

import odds.domain.Bet
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface BetRepository : ReactiveCrudRepository<Bet, Long> {
    fun findAllByPlayerId(playerId: Long?): Flux<Bet>
}