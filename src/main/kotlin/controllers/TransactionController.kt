package odds.controllers

import odds.dto.CreateTransactionDTO
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

    @GetMapping("/{username}")
    fun getPlayerTransactions(@PathVariable username: String): Flux<TransactionResponse> {
        return this.transactionService.getPlayerTransaction(username)
            .map { TransactionResponse(it.id, username, it.amount, it.type) }
    }

    @PostMapping("/deposit")
    fun depositCredit(@RequestBody createTransactionDTO: CreateTransactionDTO): Mono<TransactionResponse> {
        return this.transactionService.registerTransaction(createTransactionDTO)
            .map { TransactionResponse(it.id, createTransactionDTO.playerUsername, it.amount, it.type) }
    }
}