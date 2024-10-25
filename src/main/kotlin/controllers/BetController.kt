package odds.controllers

import odds.dto.BetResponse
import odds.dto.CreateBetRequest
import odds.services.BetService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/bet")
class BetController @Autowired constructor(
    private val betService: BetService
) {

    @PostMapping("/placeBet")
    fun placeBet(@RequestBody betRequest: CreateBetRequest): Mono<BetResponse> {
        return this.betService.placeBet(
            betRequest.username,
            betRequest.betAmount,
            betRequest.betNumber
        ).flatMap {
            Mono.just(
                BetResponse(
                    it.id,
                    it.playerId,
                    it.betAmount,
                    it.betNumber,
                    it.generatedNumber,
                    it.result,
                    it.winnings
                )
            )
        }
    }

    @GetMapping()
    fun getPlayerBets(@RequestParam username: String): Flux<BetResponse> {
        return this.betService.getPlayerBets(username)
            .map {
                BetResponse(
                    it.id, it.playerId, it.betAmount, it.betNumber,
                    it.generatedNumber, it.result, it.winnings
                )
            }
    }

}