package odds.services

import dto.enum.ResultEnum
import dto.enum.TransactionTypeEnum
import odds.domain.Bet
import odds.domain.Player
import odds.domain.Transaction
import odds.dto.CreateTransactionDTO
import odds.exceptions.InsufficientBalanceException
import odds.exceptions.SafeBetException
import odds.repositories.BetRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.math.abs

private const val BET_SAFETY_FACTOR = 0.5

private const val MULTIPLIER_FACTOR_10 = 10

private const val MULTIPLIER_FACTOR_5 = 5

private const val MULTIPLIER_FACTOR_HALF = 0.5

@Service
class BetServiceImpl @Autowired constructor(
    private val betRepository: BetRepository,
    private val playerService: PlayerService,
    private val transactionService: TransactionService
) : BetService {


    override fun getPlayerBets(username: String): Flux<Bet> {
        return this.playerService.getPlayer(username)
            .flatMapMany { player ->
                this.betRepository.findAllByPlayerId(player.id)
            }
    }


    override fun placeBet(
        username: String,
        betAmount: Double,
        betNumber: Int
    ): Mono<Bet> {
        return this.playerService.getPlayer(username)
            .flatMap { player -> validateBet(player, betAmount) }
            .flatMap { player -> processBet(player, betAmount, betNumber) }
    }


    private fun processBet(
        player: Player,
        betAmount: Double,
        betNumber: Int
    ): Mono<Bet> {
        val generatedNumber = generateRandomNumber()
        val winnings = calculateWinnings(betNumber, generatedNumber, betAmount)

        player.balance -= betAmount
        player.balance += winnings

        return this.playerService.updatePlayerAmount(player)
            .flatMap {
                val bet = Bet(
                    playerId = player.id,
                    betAmount = betAmount,
                    betNumber = betNumber,
                    generatedNumber = generatedNumber,
                    result = if (winnings > 0) ResultEnum.WIN else ResultEnum.LOSS,
                    winnings = winnings
                )

                this.betRepository.save(bet)
                    .flatMap { savedBet ->
                        registerTransaction(player.username, savedBet.betAmount).thenReturn(savedBet)
                    }

            }
    }


    private fun validateBet(player: Player, betAmount: Double): Mono<Player> {
        return if (player.balance < betAmount) {
            Mono.error(InsufficientBalanceException("Insufficient balance in ${player.surname} wallet."))
        } else if (betAmount > player.balance * BET_SAFETY_FACTOR) {
            Mono.error(SafeBetException("You cannot bet more than 50% of your(${player.username}) balance."))
        } else {
            Mono.just(player)
        }
    }


    private fun registerTransaction(username: String, amount: Double): Mono<Transaction> {
        return transactionService.registerTransaction(
            CreateTransactionDTO(
                playerUsername = username,
                amount = amount,
                type = TransactionTypeEnum.BET
            )
        )
    }


    private fun generateRandomNumber(): Int {
        return (1..MULTIPLIER_FACTOR_10).random()
    }

    private fun calculateWinnings(betNumber: Int, generatedNumber: Int, betAmount: Double): Double {
        val difference = abs((betNumber - generatedNumber).toDouble()).toInt()

        return when (difference) {
            0 -> betAmount * MULTIPLIER_FACTOR_10
            1 -> betAmount * MULTIPLIER_FACTOR_5
            2 -> betAmount * MULTIPLIER_FACTOR_HALF
            else -> 0.0
        }
    }
}