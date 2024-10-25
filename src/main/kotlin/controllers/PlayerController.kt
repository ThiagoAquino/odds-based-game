package odds.controllers

import odds.dto.CreatePlayerRequest
import odds.dto.LeaderboardResponse
import dto.PlayerResponse
import odds.services.PlayerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.QueryAnnotation
import org.springframework.data.r2dbc.repository.Query
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/player")
class PlayerController @Autowired constructor(
    private val playerService: PlayerService
) {

    @PostMapping("")
    fun registerPlayer(@RequestBody playerRequest: CreatePlayerRequest): Mono<PlayerResponse> {
        return playerService.registerPlayer(playerRequest).map {
            PlayerResponse(
                id = it.id,
                name = it.name,
                surname = it.surname,
                username = it.username,
                balance = it.balance
            )
        }
    }

    @GetMapping("")
    fun getPlayer(@RequestParam username: String): Mono<PlayerResponse> {
        return playerService.getPlayer(username)
            .map {
                PlayerResponse(
                    id = it.id,
                    name = it.name,
                    surname = it.surname,
                    username = it.username,
                    balance = it.balance
                )
            }
    }

    @GetMapping("/leaderboard")
    fun getLeaderboard(): Flux<LeaderboardResponse> {
        return playerService.getLeaderboard().log().map { player ->
            LeaderboardResponse(
                username = player.username,
                balance = player.balance
            )
        }
    }

}