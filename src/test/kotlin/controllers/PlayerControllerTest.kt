package controllers

import dto.PlayerResponse
import odds.controllers.PlayerController
import odds.domain.Player
import odds.dto.CreatePlayerDTO
import odds.dto.LeaderboardResponse
import odds.services.PlayerService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@ExtendWith(MockitoExtension::class)
class PlayerControllerTest {

    @Mock
    private lateinit var playerService: PlayerService

    @InjectMocks
    private lateinit var playerController: PlayerController

    private lateinit var webTestClient: WebTestClient

    private lateinit var player: Player

    @BeforeEach
    fun setUp() {
        this.webTestClient = WebTestClient.bindToController(playerController).build()
        this.player = Player(id = 1, name = "Maria", surname = "Silva", username = "mas", balance = 1000.0)
    }

    @Test
    fun `register player should register a player successfully`() {
        val createPlayerDTO = CreatePlayerDTO("Maria", "Silva", "mas")
        val playerResponse = PlayerResponse(
            this.player.id,
            this.player.name,
            this.player.surname,
            this.player.username,
            this.player.balance
        )

        whenever(this.playerService.registerPlayer(any())).thenReturn(Mono.just(this.player))

        this.webTestClient.post()
            .uri("/player")
            .bodyValue(createPlayerDTO)
            .exchange()
            .expectStatus().isOk
            .expectBody(PlayerResponse::class.java)
            .isEqualTo(playerResponse)

        Mockito.verify(this.playerService).registerPlayer(createPlayerDTO)
    }

    @Test
    fun `get player should return a player by username`() {
        whenever(playerService.getPlayer(this.player.username)).thenReturn(Mono.just(this.player))

        webTestClient.get()
            .uri("/player/{username}", this.player.username)
            .exchange()
            .expectStatus().isOk
            .expectBody(PlayerResponse::class.java)


        Mockito.verify(this.playerService).getPlayer(this.player.username)
    }

    @Test
    fun `get leaderboard should return a list of players`() {
        val playerList = listOf(player, Player(2, "Thiago", "Santos", "tas", 990.0))
        whenever(playerService.getLeaderboard()).thenReturn(Flux.fromIterable(playerList))

        webTestClient.get()
            .uri("/player/leaderboard")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(LeaderboardResponse::class.java)


        Mockito.verify(this.playerService).getLeaderboard()
    }
}
