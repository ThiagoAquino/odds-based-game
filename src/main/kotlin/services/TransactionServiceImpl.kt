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
        return this.playerService.getPlayer(createTransactionRequest.playerUsername) // Get player on database by username
            .flatMap { player ->                                                 // access the player inside the mono
                player.balance += createTransactionRequest.amount                   // add amount in player wallet
                this.playerService.updatePlayerAmount(player)                   // update the player balance
                    .flatMap {
                        this.transactionRepository.save(                        // create a transaction, registering this operation
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
        return this.playerService.getPlayer(username)                       // get the user, and check if exists
            .flatMapMany { player ->
                this.transactionRepository.findAllByPlayerId(player.id)     // get all transactions by user.

            }
    }
}