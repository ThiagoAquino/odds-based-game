package odds.services

import odds.domain.Transaction
import odds.dto.CreateTransactionRequest
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface TransactionService {
    fun registerTransaction(createTransactionRequest: CreateTransactionRequest): Mono<Transaction>
    fun getPlayerTransaction(username: String): Flux<Transaction>
}