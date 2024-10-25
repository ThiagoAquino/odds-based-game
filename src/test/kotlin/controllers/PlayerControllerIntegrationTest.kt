package controllers

import dto.PlayerResponse
import odds.controllers.PlayerController
import odds.domain.Player
import odds.dto.CreatePlayerRequest
import odds.dto.LeaderboardResponse
import odds.exceptions.AlreadyExistsException
import odds.exceptions.NotFoundException
import odds.repositories.BetRepository
import odds.repositories.TransactionRepository
import odds.services.PlayerService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.test.assertEquals


@WebFluxTest(controllers = [PlayerController::class])
@AutoConfigureWebTestClient
class PlayerControllerIntegrationTest {


    @Configuration
    @ComponentScan(basePackages = ["odds.controllers", "odds.services"])
    class TestConfig

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var playerService: PlayerService

    @MockBean
    private lateinit var betRepository: BetRepository

    @MockBean
    private lateinit var transactionRepository: TransactionRepository

    @Test
    fun `should register a new player`() {
        val playerRequest = CreatePlayerRequest("Maria", "Silva", "leaderboard")
        val playerResponse = PlayerResponse(1, "Maria", "Silva", "leaderboard", 1000.0)
        val player = Player(1, "Maria", "Silva", "leaderboard", 1000.0)

        whenever(this.playerService.registerPlayer(playerRequest)).thenReturn(Mono.just(player))


        this.webTestClient.post()
            .uri("/player")
            .bodyValue(playerResponse)
            .exchange()
            .expectStatus().isOk
            .expectBody(PlayerResponse::class.java)
            .value { response ->
                assertEquals(playerResponse.id, response.id)
                assertEquals(playerResponse.name, response.name)
                assertEquals(playerResponse.surname, response.surname)
                assertEquals(playerResponse.username, response.username)
                assertEquals(playerResponse.balance, response.balance)
            }
    }

    @Test
    fun `should return error when player already exists`() {
        val playerRequest = CreatePlayerRequest("Maria", "Silva", "mas")

        whenever(this.playerService.registerPlayer(playerRequest)).thenReturn(Mono.error(AlreadyExistsException("Player already exists")))

        this.webTestClient.post()
            .uri("/player")
            .bodyValue(playerRequest)
            .exchange()
            .expectStatus().is4xxClientError

    }

    @Test
    fun `should get a player by username`() {
        val username = "mas"
        val playerResponse = PlayerResponse(1, "Maria", "Silva", "mas", 1000.0)
        val player = Player(1, "Maria", "Silva", "mas", 1000.0)

        whenever(this.playerService.getPlayer(username)).thenReturn(Mono.just(player))


        this.webTestClient.get()
            .uri("/player?username=$username")
            .exchange()
            .expectStatus().isOk
            .expectBody(PlayerResponse::class.java)
            .value { response ->
                assertEquals(playerResponse.id, response.id)
                assertEquals(playerResponse.name, response.name)
                assertEquals(playerResponse.surname, response.surname)
                assertEquals(playerResponse.username, response.username)
                assertEquals(playerResponse.balance, response.balance)
            }
    }

    @Test
    fun `should get a player by username nonexistent`() {
        val username = "tas"

        whenever(this.playerService.getPlayer(username)).thenReturn(Mono.error(NotFoundException("Player $username not found.")))

        this.webTestClient.get()
            .uri("/player?username=$username")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()

    }

    @Test
    fun `should get leaderboard`() {

        val players = listOf(
            Player(1, "Maria", "Silva", "mas", 1000.0),
            Player(2, "Thiago", "Santos", "tas", 950.0)
        )

        val leaderboardResponse = listOf(
            LeaderboardResponse("mas", 1000.0),
            LeaderboardResponse("tas", 950.0)
        )

        whenever(this.playerService.getLeaderboard()).thenReturn(Flux.fromIterable(players))

        this.webTestClient.get()
            .uri("/player/leaderboard")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(LeaderboardResponse::class.java)
            .value<WebTestClient.ListBodySpec<LeaderboardResponse>> { response: List<LeaderboardResponse> ->
                assertEquals(2, response.size)
                assertEquals(leaderboardResponse[0].username, response[0].username)
                assertEquals(leaderboardResponse[0].balance, response[0].balance)
                assertEquals(leaderboardResponse[1].username, response[1].username)
                assertEquals(leaderboardResponse[1].balance, response[1].balance)
            }
    }


}
