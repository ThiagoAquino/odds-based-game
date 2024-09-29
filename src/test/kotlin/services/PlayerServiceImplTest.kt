package services

import odds.domain.Player
import odds.dto.CreatePlayerDTO
import odds.exceptions.AlreadyExistsException
import odds.exceptions.NotFoundException
import odds.repositories.PlayerRepository
import odds.services.PlayerServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier


@ExtendWith(MockitoExtension::class)
class PlayerServiceImplTest {

    @Mock
    private lateinit var playerRepository: PlayerRepository

    @InjectMocks
    private lateinit var playerServiceImpl: PlayerServiceImpl

    private lateinit var player: Player

    private lateinit var secondPlayer: Player

    @BeforeEach
    fun setUp() {
        this.player = Player(id = 1, name = "Maria", surname = "Silva", username = "mas", balance = 1000.0)
        this.secondPlayer = Player(2, "Thiago", "Santos", "tas", 500.0)
    }

    @Test
    fun `get player should return a player`() {
        whenever(this.playerRepository.findByUsername(this.player.username)).thenReturn(Mono.just(this.player))
        val result = this.playerServiceImpl.getPlayer(this.player.username)

        StepVerifier.create(result)
            .expectNextMatches { it.username == this.player.username }
            .verifyComplete()
    }

    @Test
    fun `get player should throw NotFoundException when player not found`() {

        whenever(this.playerRepository.findByUsername(this.player.username)).thenReturn(Mono.empty())
        val result = this.playerServiceImpl.getPlayer(this.player.username)

        StepVerifier.create(result)
            .expectError(NotFoundException::class.java)
            .verify()

    }

    @Test
    fun `register player should return a new player`() {
        val createPlayerDTO = CreatePlayerDTO("Thiago", "Santos", "tas")
        val newPlayer = Player(2, "Thiago", "Santos", "tas", 1000.0)
        whenever(this.playerRepository.findByUsername(createPlayerDTO.username)).thenReturn(Mono.empty())
        whenever(this.playerRepository.save(any<Player>())).thenReturn(Mono.just(newPlayer))

        val result = this.playerServiceImpl.registerPlayer(createPlayerDTO)

        StepVerifier.create(result)
            .expectNextMatches { it.username == createPlayerDTO.username }
            .verifyComplete()
    }

    @Test
    fun `register player should throw AlreadyExists when found an username`() {
        val createPlayerDTO = CreatePlayerDTO("Thiago", "Santos", "tas")

        whenever(this.playerRepository.findByUsername(createPlayerDTO.username)).thenReturn(Mono.just(this.secondPlayer))

        val result = this.playerServiceImpl.registerPlayer(createPlayerDTO)

        StepVerifier.create(result)
            .expectError(AlreadyExistsException::class.java)
            .verify()

        verify(this.playerRepository, never()).save(any())
    }

    @Test
    fun `update player player should return a player with new amount`() {
        val playerWithNewBalance = player.copy(balance = 1100.0)
        whenever(this.playerRepository.findByUsername(playerWithNewBalance.username)).thenReturn(Mono.just(this.player))
        whenever(this.playerRepository.save(playerWithNewBalance)).thenReturn(Mono.just(playerWithNewBalance))

        val result = this.playerServiceImpl.updatePlayerAmount(playerWithNewBalance)

        StepVerifier.create(result)
            .expectNextMatches { it.balance == playerWithNewBalance.balance }
            .verifyComplete()
    }

    @Test
    fun `get leaderboard should return a player's list ordered by balance`() {

        whenever(this.playerRepository.findAll()).thenReturn(Flux.just(this.player, this.secondPlayer))

        val result = this.playerServiceImpl.getLeaderboard()

        StepVerifier.create(result)
            .expectNext(this.player, this.secondPlayer)
            .verifyComplete()
    }
}