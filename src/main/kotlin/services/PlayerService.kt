package odds.services

import odds.domain.Player
import odds.dto.CreatePlayerRequest
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface PlayerService {
    fun getPlayer(username: String): Mono<Player>
    fun registerPlayer(createPlayerRequest: CreatePlayerRequest): Mono<Player>
    fun updatePlayerAmount(player: Player): Mono<Player>
    fun getLeaderboard(): Flux<Player>
}