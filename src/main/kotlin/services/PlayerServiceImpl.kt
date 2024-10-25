package odds.services

import odds.domain.Player
import odds.dto.CreatePlayerRequest
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

    /**
     * Get player by username
     */
    override fun getPlayer(username: String): Mono<Player> {
        return this.playerRepository.findByUsername(username)
            .switchIfEmpty(
                Mono.error(NotFoundException("Player $username not found."))
            )
    }

    /**
     * Create a new player
     */
    override fun registerPlayer(createPlayerRequest: CreatePlayerRequest): Mono<Player> {
        return this.playerRepository.findByUsername(createPlayerRequest.username)       // Check if the username is free
            .flatMap<Player> {
                Mono.error(AlreadyExistsException("Username ${createPlayerRequest.username} already exists."))  // if not, throw exception
            }
            .switchIfEmpty(                                                                     // if it's free, create a new player
                Mono.defer {                                                                    // used to daly the creation of the player
                    this.playerRepository.save(
                        Player(
                            name = createPlayerRequest.name,
                            surname = createPlayerRequest.surname,
                            username = createPlayerRequest.username
                        )
                    )
                }
            )
    }

    /**
     * Update the player amount
     */
    override fun updatePlayerAmount(player: Player): Mono<Player> {
        return this.getPlayer(player.username)
            .flatMap {
                it.balance = player.balance
                this.playerRepository.save(it)
            }
    }

    /**
     * Return the leaderboard.
     */
    override fun getLeaderboard(): Flux<Player> {
        return this.playerRepository.findAll()                      // get all the player
            .sort(compareByDescending { it.balance })               // order by descendent
            .take(10)                                           // limited by 10 users
    }

}