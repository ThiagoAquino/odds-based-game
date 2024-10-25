package odds.services

import odds.domain.Transaction
import odds.dto.CreateTransactionRequest
import odds.repositories.TransactionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TransactionServiceImpl @Autowired constructor(
    private val transactionRepository: TransactionRepository,
    private val playerService: PlayerService
) : TransactionService {


    /**
     * Method responsible for registering all transactions. Bet or Depósit
     */
    override fun registerTransaction(createTransactionRequest: CreateTransactionRequest): Mono<Transaction> {
        return this.playerService.getPlayer(createTransactionRequest.playerUsername)
            .flatMap { player ->
                player.balance += createTransactionRequest.amount
                this.playerService.updatePlayerAmount(player)
                    .flatMap {
                        this.transactionRepository.save(
                            Transaction(
                                playerId = player.id,
                                amount = createTransactionRequest.amount,
                                type = createTransactionRequest.type
                            )
                        )
                    }
            }

    }


    /**
     * Responsible to get all the transactions by user
     */
    override fun getPlayerTransaction(username: String): Flux<Transaction> {
        return this.playerService.getPlayer(username)
            .flatMapMany { player ->
                this.transactionRepository.findAllByPlayerId(player.id)

            }
    }
}