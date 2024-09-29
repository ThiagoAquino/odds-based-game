package odds.services

import odds.domain.Bet
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface BetService {

    fun getPlayerBets(username: String): Flux<Bet>

    fun placeBet(username: String, betAmount: Double, betNumber: Int): Mono<Bet>
}