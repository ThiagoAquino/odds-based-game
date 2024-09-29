package odds.services

import odds.domain.Player
import odds.dto.CreatePlayerDTO
import odds.exceptions.AlreadyExistsException
import odds.exceptions.NotFoundException
import odds.repositories.PlayerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class PlayerServiceImpl @Autowired constructor(
    private val playerRepository: PlayerRepository
) : PlayerService {

    override fun getPlayer(username: String): Mono<Player> {
        return this.playerRepository.findByUsername(username)
            .switchIfEmpty(
                Mono.error(NotFoundException("Player $username not found."))
            )
    }

    override fun registerPlayer(createPlayerDTO: CreatePlayerDTO): Mono<Player> {
        return this.playerRepository.findByUsername(createPlayerDTO.username)
            .flatMap<Player> {
                Mono.error(AlreadyExistsException("Username ${createPlayerDTO.username} already exists."))
            }
            .switchIfEmpty(
                Mono.defer {
                    this.playerRepository.save(
                        Player(
                            name = createPlayerDTO.name,
                            surname = createPlayerDTO.surname,
                            username = createPlayerDTO.username
                        )
                    )
                }
            )
    }

    override fun updatePlayerAmount(player: Player): Mono<Player> {
        return this.getPlayer(player.username)
            .flatMap {
                it.balance = player.balance
                this.playerRepository.save(it)
            }
    }

    override fun getLeaderboard(): Flux<Player> {
        return this.playerRepository.findAll()
            .sort(compareByDescending { it.balance })
            .take(10)
    }

}