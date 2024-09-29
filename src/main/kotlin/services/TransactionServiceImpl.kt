package odds.services

import odds.domain.Transaction
import odds.dto.CreateTransactionDTO
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

    override fun registerTransaction(createTransactionDTO: CreateTransactionDTO): Mono<Transaction> {
        return this.playerService.getPlayer(createTransactionDTO.playerUsername)
            .flatMap { player ->
                player.balance += createTransactionDTO.amount
                this.playerService.updatePlayerAmount(player)
                    .flatMap {
                        this.transactionRepository.save(
                            Transaction(
                                playerId = player.id,
                                amount = createTransactionDTO.amount,
                                type = createTransactionDTO.type
                            )
                        )
                    }
            }

    }

    override fun getPlayerTransaction(username: String): Flux<Transaction> {
        return this.playerService.getPlayer(username)
            .flatMapMany { player ->
                this.transactionRepository.findAllByPlayerId(player.id)

            }
    }
}