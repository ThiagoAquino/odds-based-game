package odds.services

import odds.domain.Transaction
import odds.dto.CreateTransactionDTO
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface TransactionService {
    fun registerTransaction(createTransactionDTO: CreateTransactionDTO): Mono<Transaction>
    fun getPlayerTransaction(username: String): Flux<Transaction>
}