package services

import dto.enum.TransactionTypeEnum
import odds.domain.Player
import odds.domain.Transaction
import odds.dto.CreateTransactionDTO
import odds.repositories.TransactionRepository
import odds.services.PlayerService
import odds.services.TransactionServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier


@ExtendWith(MockitoExtension::class)
class TransactionServiceImplTest {

    @Mock
    private lateinit var playerService: PlayerService

    @Mock
    private lateinit var transactionRepository: TransactionRepository

    @InjectMocks
    private lateinit var transactionServiceImpl: TransactionServiceImpl

    private lateinit var transaction: Transaction

    private lateinit var player: Player

    private lateinit var createTransactionDTO: CreateTransactionDTO


    @BeforeEach
    fun setUp() {
        this.player = Player(id = 1, name = "Maria", surname = "Silva", username = "mas", balance = 1000.0)

        this.transaction =
            Transaction(id = 1, playerId = this.player.id, amount = 100.0, type = TransactionTypeEnum.BET)

        this.createTransactionDTO =
            CreateTransactionDTO(playerUsername = this.player.username, amount = 100.0, type = TransactionTypeEnum.BET)
    }

    @Test
    fun `register transaction should return a new transaction`() {
        whenever(this.playerService.getPlayer(this.player.username)).thenReturn(Mono.just(this.player))
        whenever(this.playerService.updatePlayerAmount(any())).thenReturn(Mono.just(this.player))
        whenever(this.transactionRepository.save(any())).thenReturn(Mono.just(this.transaction))

        val result = this.transactionServiceImpl.registerTransaction(this.createTransactionDTO)

        StepVerifier.create(result)
            .expectNextMatches { it.amount == this.createTransactionDTO.amount }
            .verifyComplete()

    }

    @Test
    fun `get players transactions should return player's transactions`() {
        whenever(this.playerService.getPlayer(this.player.username)).thenReturn(Mono.just(this.player))
        whenever(this.transactionRepository.findAllByPlayerId(any())).thenReturn(Flux.just(this.transaction))

        val result = this.transactionServiceImpl.getPlayerTransaction(this.player.username)

        StepVerifier.create(result)
            .expectNext(transaction)
            .verifyComplete()
    }
}