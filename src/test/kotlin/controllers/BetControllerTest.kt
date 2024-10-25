package controllers

import dto.enum.ResultEnum
import odds.controllers.BetController
import odds.domain.Bet
import odds.domain.Player
import odds.dto.BetResponse
import odds.dto.CreateBetRequest
import odds.services.BetService
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
class BetControllerTest {

    @Mock
    private lateinit var betService: BetService

    @InjectMocks
    private lateinit var betController: BetController

    private lateinit var webTestClient: WebTestClient

    private lateinit var bet: Bet

    private lateinit var player: Player

    @BeforeEach
    fun setUp() {
        this.webTestClient = WebTestClient.bindToController(this.betController).build()

        this.player = Player(id = 1, name = "Maria", surname = "Silva", username = "mas", balance = 1000.0)

        this.bet = Bet(
            id = 1,
            playerId = 1,
            betAmount = 100.0,
            betNumber = 5,
            generatedNumber = 3,
            result = ResultEnum.WIN,
            winnings = 500.0
        )
    }

    @Test
    fun `place bet should place a bet and return BetResponse`() {
        val createBetRequest = CreateBetRequest(this.player.username, 100.0, 5)
        val betResponse = BetResponse(
            id = this.bet.id,
            playerId = this.bet.playerId,
            betAmount = this.bet.betAmount,
            betNumber = this.bet.betNumber,
            generatedNumber = this.bet.generatedNumber,
            result = this.bet.result,
            winnings = this.bet.winnings
        )

        whenever(this.betService.placeBet(any(), any(), any())).thenReturn(Mono.just(this.bet))

        this.webTestClient.post()
            .uri("/bet/placeBet")
            .bodyValue(createBetRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody(BetResponse::class.java)
            .isEqualTo(betResponse)

        Mockito.verify(this.betService).placeBet(createBetRequest.username, createBetRequest.betAmount, createBetRequest.betNumber)
    }

    @Test
    fun `get player's bets should return a list of BetResponse`() {
        whenever(this.betService.getPlayerBets(this.player.username)).thenReturn(Flux.just(this.bet))

        this.webTestClient.get()
            .uri("/bet/{username}", this.player.username)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(BetResponse::class.java)
            .hasSize(1)
            .contains(
                BetResponse(
                    id = this.bet.id,
                    playerId = this.bet.playerId,
                    betAmount = this.bet.betAmount,
                    betNumber = this.bet.betNumber,
                    generatedNumber = this.bet.generatedNumber,
                    result = this.bet.result,
                    winnings = this.bet.winnings
                )
            )

        Mockito.verify(this.betService).getPlayerBets(this.player.username)
    }
}
