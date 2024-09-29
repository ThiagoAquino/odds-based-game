package odds.services

import odds.domain.Player
import odds.dto.CreatePlayerDTO
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface PlayerService {
    fun getPlayer(username: String): Mono<Player>
    fun registerPlayer(createPlayerDTO: CreatePlayerDTO): Mono<Player>
    fun updatePlayerAmount(player: Player): Mono<Player>
    fun getLeaderboard(): Flux<Player>
}