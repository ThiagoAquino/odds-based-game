package odds.repositories

import odds.domain.Transaction
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface TransactionRepository : ReactiveCrudRepository <Transaction, Long> {
    fun findAllByPlayerId(playerId: Long?): Flux<Transaction>
    fun save(transaction: Transaction): Mono<Transaction>
}