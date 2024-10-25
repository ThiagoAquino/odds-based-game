package odds.controllers

import odds.dto.CreateTransactionRequest
import odds.dto.TransactionResponse
import odds.services.TransactionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/transaction")
class TransactionController @Autowired constructor(
    private val transactionService: TransactionService
) {

    @GetMapping()
    fun getPlayerTransactions(@RequestParam username: String): Flux<TransactionResponse> {
        return this.transactionService.getPlayerTransaction(username)
            .map { TransactionResponse(it.id, username, it.amount, it.type) }
    }

    @PostMapping("/deposit")
    fun depositCredit(@RequestBody createTransactionRequest: CreateTransactionRequest): Mono<TransactionResponse> {
        return this.transactionService.registerTransaction(createTransactionRequest)
            .map { TransactionResponse(it.id, createTransactionRequest.playerUsername, it.amount, it.type) }
    }
}