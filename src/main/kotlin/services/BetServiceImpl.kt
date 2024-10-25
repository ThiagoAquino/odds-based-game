package odds.services

import dto.enum.ResultEnum
import dto.enum.TransactionTypeEnum
import odds.domain.Bet
import odds.domain.Player
import odds.domain.Transaction
import odds.dto.CreateTransactionRequest
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


    /**
     * Get all bets by player
     */
    override fun getPlayerBets(username: String): Flux<Bet> {
        return this.playerService.getPlayer(username)
            .flatMapMany { player ->
                this.betRepository.findAllByPlayerId(player.id)
            }
    }

    /**
     * Make a bet
     */
    override fun placeBet(
        username: String,
        betAmount: Double,
        betNumber: Int
    ): Mono<Bet> {
        return this.playerService.getPlayer(username)
            .flatMap { player -> validateBet(player, betAmount) }                   // player validate
            .flatMap { player -> processBet(player, betAmount, betNumber) }         // make a bet
    }


    /**
     * Process a bet
     */

    private fun processBet(
        player: Player,
        betAmount: Double,
        betNumber: Int
    ): Mono<Bet> {
        val generatedNumber = generateRandomNumber()                                        // get  the random number
        val winnings = calculateWinnings(betNumber, generatedNumber, betAmount)             // calculate the result

        player.balance -= betAmount                                                         // remove the bet value
        player.balance += winnings                                                          // add the winning value

        return this.playerService.updatePlayerAmount(player)                                // update the player amount
            .flatMap {
                val bet = Bet(                                                              //create a betting register
                    playerId = player.id,
                    betAmount = betAmount,
                    betNumber = betNumber,
                    generatedNumber = generatedNumber,
                    result = if (winnings > 0) ResultEnum.WIN else ResultEnum.LOSS,
                    winnings = winnings
                )

                this.betRepository.save(bet)                                                     //create a player transaction
                    .flatMap { savedBet ->
                        registerTransaction(player.username, savedBet.betAmount).thenReturn(savedBet)
                    }

            }
    }


    private fun validateBet(player: Player, betAmount: Double): Mono<Player> {
        return if (player.balance < betAmount) {
            Mono.error(InsufficientBalanceException("Insufficient balance in ${player.surname} wallet."))       // check if has the enough money
        } else if (betAmount > player.balance * BET_SAFETY_FACTOR) {
            Mono.error(SafeBetException("You cannot bet more than 50% of your(${player.username}) balance."))   // security conditions, not allow bet more than 50% of the balance
        } else {
            Mono.just(player)
        }
    }


    private fun registerTransaction(username: String, amount: Double): Mono<Transaction> {
        return transactionService.registerTransaction(                          // register a new transaction
            CreateTransactionRequest(
                playerUsername = username,
                amount = amount,
                type = TransactionTypeEnum.BET
            )
        )
    }


    private fun generateRandomNumber(): Int {
        return (1..MULTIPLIER_FACTOR_10).random()                   //generate a random number
    }

    private fun calculateWinnings(betNumber: Int, generatedNumber: Int, betAmount: Double): Double {
        val difference = abs((betNumber - generatedNumber).toDouble()).toInt()      // calculate the difference between the player's number and the random number

        return when (difference) {
            0 -> betAmount * MULTIPLIER_FACTOR_10                                   // if the distance is 0, player win 10x the bet value
            1 -> betAmount * MULTIPLIER_FACTOR_5                                    // if the distance is 1/-1, player win 5x the bet value
            2 -> betAmount * MULTIPLIER_FACTOR_HALF                                 // if the distance is 2/-2, player win 0.5x the bet value
            else -> 0.0
        }
    }
}