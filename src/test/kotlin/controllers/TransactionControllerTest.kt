package controllers

import dto.enum.TransactionTypeEnum
import odds.controllers.TransactionController
import odds.domain.Player
import odds.domain.Transaction
import odds.dto.CreateTransactionRequest
import odds.dto.TransactionResponse
import odds.services.TransactionService
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
class TransactionControllerTest {

    @Mock
    private lateinit var transactionService: TransactionService

    @InjectMocks
    private lateinit var transactionController: TransactionController

    private lateinit var webTestClient: WebTestClient

    private lateinit var transaction: Transaction

    private lateinit var createTransactionRequest: CreateTransactionRequest

    private lateinit var player: Player

    @BeforeEach
    fun setUp() {
        this.webTestClient = WebTestClient.bindToController(this.transactionController).build()

        this.player = Player(id = 1, name = "Maria", surname = "Silva", username = "mas", balance = 1000.0)

        this.transaction = Transaction(
            id = 1,
            playerId = this.player.id,
            amount = 200.0,
            type = TransactionTypeEnum.DEPOSIT
        )

        this.createTransactionRequest = CreateTransactionRequest(
            playerUsername = this.player.username,
            amount = 200.0,
            type = TransactionTypeEnum.DEPOSIT
        )
    }

    @Test
    fun `get player's transactions should return transactions for a player`() {
        whenever(this.transactionService.getPlayerTransaction(this.player.username)).thenReturn(Flux.just(this.transaction))

        this.webTestClient.get()
            .uri("/transaction/{username}", this.player.username)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(TransactionResponse::class.java)

        Mockito.verify(this.transactionService).getPlayerTransaction(this.player.username)
    }


    @Test
    fun `deposit credit should create a transaction and return the transaction response`() {
        val transactionResponse =
            TransactionResponse(
                this.transaction.id,
                this.createTransactionRequest.playerUsername,
                this.transaction.amount,
                this.transaction.type
            )

        whenever(this.transactionService.registerTransaction(any())).thenReturn(Mono.just(this.transaction))

        this.webTestClient.post()
            .uri("/transaction/deposit")
            .bodyValue(this.createTransactionRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody(TransactionResponse::class.java)
            .isEqualTo(transactionResponse)

        Mockito.verify(this.transactionService).registerTransaction(this.createTransactionRequest)
    }
}
